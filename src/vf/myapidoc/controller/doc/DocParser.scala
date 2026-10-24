package vf.myapidoc.controller.doc

import utopia.access.model.ContentType
import utopia.access.model.enumeration.ContentCategory.{Application, Text}
import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.collection.immutable.{Empty, OptimizedIndexedSeq, Pair, Single}
import utopia.flow.collection.mutable.iterator.{OptionsIterator, PollingIterator}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.string.{Lines, Regex}
import utopia.flow.util.StringExtensions._
import utopia.flow.util.UncertainBoolean
import utopia.scribe.core.controller.logging.Scribe
import vf.myapidoc.model.cached._
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.util.Common._

import java.nio.file.Path

/**
 * An interface for parsing API documents.
 * @author Mikko Hilpinen
 * @since 15.10.2026, v0.1
 */
object DocParser
{
	// ATTRIBUTES   --------------------
	
	private val scribe: Scribe = newScribe("DocParser")
	
	private val indentationR = Regex.whitespace || Regex.escape('\t')
	private val indentedLineR = indentationR.withinParentheses + Regex.any
	
	private val listElementMarkerR = Regex.dash || (Regex.digit.oneOrMoreTimes + Regex.dot).withinParentheses
	private val startsWithListElementMarkerR = listElementMarkerR + Regex.any
	private val newListItemR = indentationR.withinParentheses.anyTimes + startsWithListElementMarkerR
	private val emptyLineR = indentationR.withinParentheses.anyTimes
	
	private val andR = Regex("AND") || Regex.escape('&').times(2)
	private val orR = Regex("OR|or") || Regex.escape('|').times(2)
	
	
	// OTHER    ------------------------
	
	/**
	 * Parses API node documentation from an MD file
	 * @param path Path to the file to read
	 * @param groupHeaderLevel Header level that lists API node groups.
	 *                         Default = 2.
	 * @return Parsed API node groups. Failure if file-reading failed.
	 */
	def readApiNodesFrom(path: Path, groupHeaderLevel: Int = 2) =
		Lines.iterate(path) { linesIter =>
			val groupsBuilder = OptimizedIndexedSeq.newBuilder[NewApiNodeGroup]
			
			// Finds the first header
			linesIter.findMap { line =>
				val headerDepth = line.iterator.takeWhile { _ == '#' }.size
				if (headerDepth >= groupHeaderLevel) Some(headerDepth -> line.drop(headerDepth).trim) else None
			} match {
				case Some((firstHeaderDepth, firstHeader)) =>
					// Continually parses the content one (node group) section at a time
					var nextHeaderDepth = firstHeaderDepth
					var nextHeader = firstHeader
					while (linesIter.hasNext) {
						val (section, stopHeader) = parseNextSection(nextHeader, linesIter, nextHeaderDepth)
						
						// Processes subsections as API node docs and adds this section / group to the builder
						val (sectionHeader, sectionLines) = section.value
						groupsBuilder += NewApiNodeGroup(sectionHeader,
							nodes = section.children.map(processApiNodeFrom),
							description = sectionLines.dropRightWhile { _.isEmpty }.mkString("\n"))
						
						// Prepares to continue to the next group
						stopHeader.foreach { case (depth, header) =>
							nextHeader = header
							nextHeaderDepth = depth
						}
					}
				// Case: No headers found => Logs a warning and closes
				case None =>
					import utopia.flow.parse.file.FileExtensions._
					scribe.in("read.apiNodes.noHeaders").warning("No headers found", Model.from("file" -> path.toJson))
			}
			
			groupsBuilder.result()
		}
	
	/**
	 * Parses all models listed in a document.
	 * Assumes that the models are named in the 2nd level headers, and that each contains a list named "properties"
	 * @param path Path to the file to read
	 * @return All read models. A failure if file-reading failed.
	 */
	def readModelsFrom(path: Path) =
		Lines.iterate(path) { linesIter =>
			val pollingLinesIter = linesIter.pollable
			// NB: Current version assumes that all models are listed at header level 2
			// Correct if necessary
			OptionsIterator.continually { pollingLinesIter.find { _.startsWith("##") } }
				.flatMap { headerLine => processModel(headerLine.dropWhile { _ == '#' }.trim, pollingLinesIter) }
				.toOptimizedSeq
		}
		
	private def processApiNodeFrom(section: ValueTree[(String, Seq[String])]) = {
		val (name, lines) = section.value
		
		// Finds the portion of this section that concerns this API node generally
		val linesWithoutBold = lines.map { _.filterNot { _ == '*' } }
		val methodLineIndex = linesWithoutBold.findIndexWhere { _.startsWithIgnoreCase("Method:") }
		
		// Extracts and processes the listed definitions
		var parentName = ""
		var path = ""
		var pathParams: Seq[NewProperty] = Empty
		val (description, tokenOptions, scope) = processDefinitions(methodLineIndex match {
			case Some(i) => linesWithoutBold.view.take(i)
			case None => linesWithoutBold
		}) {
			(key, lines) =>
				key match {
					case "parent" =>
						parentName = lines.head
						true
					case "path" =>
						path = lines.head
						true
					case "pathparameters" | "pathparams" =>
						pathParams = parseProps(lines.iterator.pollable)
						true
					case _ => false
				}
		}
		
		val methodImplementations = methodLineIndex match {
			// Case: The method section is started using a "Method:" -line
			//       => Parses the remaining lines as part of that method's implementation
			case Some(headerIndex) =>
				val (methodNamePart, descriptionPart) =
					linesWithoutBold(headerIndex).drop(7).splitAtFirst(":").map { _.trim }.toTuple
				val results = parseMethodImplementation(name, methodNamePart, linesWithoutBold.drop(headerIndex + 1))
				
				// Includes the description from the header / method line, if given
				if (results.nonEmpty && descriptionPart.nonEmpty)
					results.map {
						_.mapDescription { d => if (d.isEmpty) descriptionPart else s"$descriptionPart\n\n$d" }
					}
				else
					results
				
			// Case: No "Method:" -line => Checks for method-subsections
			case None =>
				section.children.flatMap { methodSection =>
					val (sectionName, lines) = methodSection.value
					parseMethodImplementation(name, sectionName, lines)
				}
		}
		
		// Combines collected information into a single model
		NewApiNode(name, path, parentName, pathParams, tokenOptions, scope, methodImplementations, description)
	}
	
	private def parseMethodImplementation(nodeName: => String, header: String, lines: Seq[String]) = {
		val methods = parseMethod(nodeName, header)
		if (methods.isEmpty)
			Empty
		else {
			val implementation = processMethodImplementation(nodeName, methods.head, lines)
			implementation +: methods.tail.map(implementation.withMethod)
		}
	}
	private def parseMethod(nodeName: => String, methodHeader: String): Seq[Method] = {
		val result = methodHeader.split('|').iterator
			.map { _.trim }.filter { _.forall { _.isLetter } }.map(Method.apply).toOptimizedSeq
		
		if (result.isEmpty)
			scribe.in("parse.method").recoverable("Unrecognized method header",
				Model.from("node" -> nodeName, "header" -> methodHeader))
			
		result
	}
	
	private def processMethodImplementation(nodeName: => String, method: Method, lines: Seq[String]) = {
		var queryParams: Seq[NewProperty] = Empty
		var requestBodyOptions: Seq[NewBodyDefinition] = Empty
		var optionalRequestBody = false
		var statusCodes: Seq[(Int, String)] = Empty
		var responseBody: Option[NewBodyDefinition] = None
		var emptyResponse = false
		val (description, tokenOptions, scopes) = processDefinitions(lines,
			Model.from("node" -> nodeName, "method" -> method.name)) {
			(key, lines) =>
				key match {
					case "queryparameters" | "queryparams" =>
						queryParams = parseProps(lines.iterator.pollable)
						true
					case "requestbody" | "request" =>
						if (newListItemR(lines.head)) {
							val bodyOptions = parseList(lines).map(processBodyEntry)
							optionalRequestBody = bodyOptions.exists { _.isEmpty }
							requestBodyOptions = bodyOptions.flatten
						}
						else
							processBodyEntry(ValueTree(lines.head).withChildren(parseList(lines.view.tail))) match {
								case Some(body) => requestBodyOptions = Single(body)
								case None => optionalRequestBody = true
							}
						true
						
					case "requestbodyoptions" =>
						val bodyOptions = parseList(lines).map(processBodyEntry)
						optionalRequestBody = bodyOptions.exists { _.isEmpty }
						requestBodyOptions = bodyOptions.flatten
						true
					case "response" =>
						val responseDetails = parseList(lines.view.tail)
						// Parses the response status entries, if present
						responseDetails.find { _.value.startsWithIgnoreCase("Status") }.foreach { statusesNode =>
							statusCodes = statusesNode.children.flatMap { statusNode =>
								// Case: Too short input => Logs and ignores
								if (statusNode.value.length < 3) {
									scribe.in("process.nodeMethodImplementation.response.status.tooShort").recoverable(
										"Invalid status code input", Model.from("node" -> nodeName,
											"method" -> method.name, "input" -> statusNode.value))
									None
								}
								else {
									val (numberPart, remainder) = statusNode.value.splitAt(3)
									numberPart.int match {
										// Case: Status code listed => Extracts and includes the description
										case Some(code) =>
											val extraDetails = listToString(statusNode.children)
											val description = Pair(remainder.drop(1).trim, extraDetails)
												.filter { _.nonEmpty }.mkString(" \n")
											Some(code -> description)
										
										// Case: Expected a status code but not found => Logs and ignores
										case None =>
											scribe.in("process.nodeMethodImplementation.response.status.invalid")
												.recoverable("Invalid status code input",
													Model.from("node" -> nodeName, "method" -> method.name,
														"input" -> statusNode.value))
											None
									}
								}
							}
						}
						// Parses the response body definition, if present
						responseDetails.find { _.value.startsWithIgnoreCase("Body:") }.foreach { bodyNode =>
							processBodyEntry(bodyNode.mapLocalValue { _.drop(5).trim }) match {
								case body: Some[NewBodyDefinition] => responseBody = body
								case None => emptyResponse = true
							}
						}
						true
					case _ => false
				}
		}
		// Combines the collected information
		NewMethodImplementation(method = method, tokenOptions = tokenOptions, scopes = scopes,
			queryParams = queryParams, requestBodyOptions = requestBodyOptions, statusCodes = statusCodes,
			responseBody = responseBody, description = description,
			responseHasBody = if (emptyResponse) false else if (responseBody.isDefined) true else UncertainBoolean,
			requestBodyIsOptional = optionalRequestBody || requestBodyOptions.isEmpty)
	}
	
	/**
	 * Extracts and processes so-called definitions from an input text, collecting standard / commonly used values
	 * @param lines Lines from which definitions are extracted
	 * @param loggingContext Details to include in logging entries (default = empty)
	 * @param processOther A function that receives a non-standard definition.
	 *                     Yields whether the definition was processed.
	 *                     If yields false, the definition will be included in the description.
	 * @return Returns 3 processed values:
	 *         1. Collected description
	 *         1. Collected auth token options, where each entry contains:
	 *              1. Token name
	 *              1. Use-case description or other additional notes
	 *         1. Collected alternative auth scope options, where each contains:
	 *              1. All 1 or more scopes that form this requirement (when combined with AND)
	 *              1. A description, which may be empty
	 */
	private def processDefinitions(lines: IterableOnce[String], loggingContext: => Model = Model.empty)
	                              (processOther: (String, Seq[String]) => Boolean) =
	{
		val definitions = parseDefinitions(lines)
		var description = ""
		var tokenOptions: Seq[(String, String)] = Empty
		var scope: Seq[(Seq[String], String)] = Empty
		definitions.foreach { case (key, lines) =>
			key match {
				case "scope" | "scopeoptions" =>
					if (scope.isEmpty) {
						// Checks whether the scopes are listed as list items or as a single entry
						val firstListItemIndex = lines.indexWhere(newListItemR.apply)
						// Case: Listed scopes => Parses multiple scopes
						if (firstListItemIndex == 0)
							scope = parseList(lines).map(parseScope)
						// Case: Single scope entry => Parses a single scope
						else {
							// Checks whether details are included as list items
							val (primaryEntry, listDetails) = {
								if (firstListItemIndex < 0)
									lines.mkString("\n") -> Empty
								else {
									val (primaryEntryLines, listLines) = lines.splitAt(firstListItemIndex)
									primaryEntryLines.mkString("\n") -> parseList(listLines)
								}
							}
							scope = Single(parseScope(ValueTree(primaryEntry).withChildren(listDetails)))
						}
					}
					// Case: Duplicate scope entry => Logs and ignores
					else
						scribe.in("process.apiNode.definition.scope.duplicate")
							.recoverable("Scope was defined multiple times", loggingContext)
				case "authorization" | "auth" =>
					if (tokenOptions.isEmpty) {
						val firstLine = lines.head
						if (newListItemR(firstLine))
							tokenOptions = parseList(lines).map { entry => entry.value -> listToString(entry.children) }
						else
							tokenOptions = Single(lines.head -> listToString(parseList(lines.view.tail)))
					}
					else
						scribe.in("process.apiNode.definition.auth.duplicate")
							.recoverable("Authorization options were defined multiple times", loggingContext)
				case "authorizationoptions" | "authoptions" =>
					if (tokenOptions.isEmpty)
						tokenOptions = parseList(lines).map { entry => entry.value -> listToString(entry.children) }
					else
						scribe.in("process.apiNode.definition.authOptions.duplicate")
							.recoverable("Authorization options were defined multiple times", loggingContext)
				case "description" =>
					description = s"${ description.appendIfNotEmpty("\n\n") }${ lines.mkString(" \n") }"
				case other =>
					if (!processOther(other, lines))
						description = s"${ description.appendIfNotEmpty("\n\n") }${ other.capitalize }: ${
							lines.mkString(" \n") }"
			}
		}
		(description, tokenOptions, scope)
	}
	
	private def processBodyEntry(entry: ValueTree[String]) = {
		val key = removeListMarkerFrom(entry.value.trim)._1
		// Case: Empty => No body is included
		if (key ~== "Empty")
			None
		else {
			// Extracts a custom content type, if specified
			val (withoutContentType, customContentType) = {
				if (key.startsWithIgnoreCase("NDJSON:"))
					(key.drop(6).trim, Some(Application.ndJson))
				else if (key.startsWithIgnoreCase("CSV:"))
					(key.drop(4).trim, Some(Text.csv))
				else {
					val firstSplitIndex = key.indexOf(':')
					if (firstSplitIndex < 0)
						(key, None)
					else {
						val (firstPart, remainder) = key.splitAt(firstSplitIndex)
						val contentType = {
							if (firstPart.contains('/'))
								ContentType.parse(firstPart)
							else
								None
						}
						(if (contentType.isDefined) remainder.drop(1).trim else key, contentType)
					}
				}
			}
			// Extracts an "Array:" prefix, if present
			val (mainKeyPart, isArray) = {
				if (withoutContentType.startsWithIgnoreCase("Array:"))
					(withoutContentType.drop(6).trim, true)
				else
					(withoutContentType, false)
			}
			// Processes the body content (object) type
			val (properties, referencedType, details) = {
				// Case: Unnamed object => Parses the listed properties
				if (mainKeyPart.startsWithIgnoreCase("Object"))
					(entry.children.map(processProp), "", "")
				// Case: Named object reference => Keeps the reference and includes possible list elements as details
				else
					(Empty, mainKeyPart, listToString(entry.children))
			}
			Some(NewBodyDefinition(properties = properties, referencedType = referencedType,
				contentType = customContentType, details = details, receivesArray = isArray))
		}
	}
	
	private def processModel(modelName: String, linesIter: PollingIterator[String]) = {
		// Looks for the properties -entry
		val descriptionLines = linesIter.takeWhile { !_.startsWithIgnoreCase("Properties") }.map { _.trim }
			.toOptimizedSeq
		if (linesIter.hasNext) {
			// Parses the listed properties
			Some(NewNamedModel(name = modelName, properties = parseProps(linesIter),
				description = descriptionLines.view.dropRightWhile { _.isEmpty }.mkString(" \n")))
		}
		// Case: No properties-property found => Logs a warning and ignores this entry
		else {
			scribe.in("process.model").warning("No properties found", Model.from("header" -> modelName))
			None
		}
	}
	
	private def parseProps(linesIter: PollingIterator[String]): Seq[NewProperty] = parseList(linesIter).map(processProp)
	private def processProp(propInput: ValueTree[String]): NewProperty = {
		val propName = propInput.value
		
		// Collects information from the nested details
		val typesBuilder = OptimizedIndexedSeq.newBuilder[PropertyType]
		var referencedType = ""
		var optional = false
		var array = false
		val descriptionBuilder = OptimizedIndexedSeq.newBuilder[ValueTree[String]]
		var children: Seq[NewProperty] = Empty
		var possibleValues: Map[String, ValueTree[String]] = Map()
		
		propInput.children.foreach { detail =>
			// Checks for key-value syntax
			val parts = detail.value.splitAtFirst(":").map { _.trim }
			val value = parts.second
			parts.first.filter { _.isLetter }.toLowerCase match {
				case "type" =>
					if (value.startsWithIgnoreCase("Array")) {
						array = true
						value.afterFirst(":").trim.ifNotEmpty.foreach { contentType =>
							PropertyType.findForName(contentType) match {
								case Some(contentType) => typesBuilder += contentType
								case None => referencedType = contentType
							}
						}
					}
					else
						PropertyType.findForName(value) match {
							case Some(contentType) => typesBuilder += contentType
							case None => referencedType = value
						}
				case "optional" => optional = true
				case "description" => descriptionBuilder += detail.withValue(value)
				case "possiblevalues" =>
					if (possibleValues.nonEmpty)
						scribe.in("process.prop.detail.possibleValues.duplicate").recoverable(
							"Possible property values were defined multiple times", Model.from("prop" -> propName))
					else
						possibleValues = detail.children.iterator
							.map { possibleValue =>
								val parts = possibleValue.value.splitAtFirst(":").map { _.trim }
								parts.first -> possibleValue.withValue(parts.second)
							}
							.toMap
					
				case "properties" | "props" =>
					if (children.nonEmpty)
						scribe.in("process.prop.detail.properties.duplicate").recoverable(
							"Properties were defined multiple times", Model.from("prop" -> propName))
					else
						children = detail.children.map(processProp)
					
				case _ => descriptionBuilder += detail
			}
		}
		
		// Compiles the property
		NewProperty(name = propName, dataTypes = typesBuilder.result(), referencedType = referencedType,
			description = listToString(descriptionBuilder.result()), children = children,
			possibleValues = possibleValues, isOptional = optional, isArray = array)
	}
	
	/**
	 * Parses a scope entry
	 * @param scopeEntry A scope entry, possibly containing additional (nested) list items
	 * @return Returns 2 values:
	 *         1. Scopes that together form the required scope (joined by AND)
	 *         1. An included description
	 */
	private def parseScope(scopeEntry: ValueTree[String]) = {
		val nestedDescription = {
			if (scopeEntry.hasChildren)
				listToString(scopeEntry.children)
			else
				""
		}
		// Checks whether multiple scopes are joined
		val parts = scopeEntry.value.split(andR)
		// Case: Multiple scopes are required => Parses each part, expecting no description to be included
		if (parts.size > 1)
			parts.map { _.notSurroundedWith("(", ")") } -> nestedDescription
		// Case: Only a single scope mentioned => Extracts the description from the scope
		else {
			val (scope, directDescription) = _parseScope(parts.head)
			Single(scope) -> Pair(directDescription, nestedDescription).filter { _.nonEmpty }.mkString(" \n")
		}
	}
	private def _parseScope(scope: String) = {
		if (scope.startsWith("`")) {
			val parts = scope.tail.splitAtFirst("`")
			parts.first -> parts.second.trim.notStartingWith(", ")
		}
		else
			scope -> ""
	}
	
	/**
	 * Converts lines into MD sections
	 * @param header This section's header
	 * @param linesIter An iterator that yields all the remaining lines.
	 *                  Will be left at the first line of the following section, if applicable.
	 * @param currentDepth The depth of the current header (starting from 1)
	 * @return Returns two values:
	 *         1. This section's data as a tree
	 *              - Each node contains the header name and the associated text lines.
	 *         1. Header level & header text, at which parsing ended.
	 *            This represents the next starting header outside this section.
	 *              - None if the end of the document was reached.
	 */
	private def parseNextSection(header: String, linesIter: Iterator[String],
	                             currentDepth: Int = 1): (ValueTree[(String, Seq[String])], Option[(Int, String)]) =
	{
		// Reads lines until the targeted section ends or until the end of the document is reached
		val linesBuilder = OptimizedIndexedSeq.newBuilder[String]
		val subsectionsBuilder = OptimizedIndexedSeq.newBuilder[ValueTree[(String, Seq[String])]]
		var stop: Option[(Int, String)] = None
		while (stop.isEmpty && linesIter.hasNext) {
			val line = linesIter.next()
			val headerDepth = line.iterator.takeWhile { _ == '#' }.size
			// Case: Section ends => Returns
			if (headerDepth >= currentDepth)
				stop = Some((headerDepth, line.drop(headerDepth).trim))
			// Case: A subsection starts => Reads it using recursion
			else if (headerDepth > 0) {
				// Continues reading the subsections until the current (main) section ends
				// or the end of the document is reached
				var nextChildDepth = headerDepth
				var nextChildHeader = line.drop(headerDepth).trim
				while (stop.isEmpty && linesIter.hasNext) {
					val (subsection, childStop) = parseNextSection(header = nextChildHeader,
						linesIter = linesIter, currentDepth = nextChildDepth)
					subsectionsBuilder += subsection
					
					childStop.foreach { case (nextSectionDepth, nextSectionHeader) =>
						if (nextSectionDepth >= currentDepth)
							stop = childStop
						else {
							nextChildDepth = nextSectionDepth
							nextChildHeader = nextSectionHeader
						}
					}
				}
			}
			// Case: Normal content row => Adds it to the current section
			else
				linesBuilder += line
		}
		
		// Returns the parsed section and the stopping header
		ValueTree[(String, Seq[String])](header -> linesBuilder.result()).withChildren(subsectionsBuilder.result()) ->
			stop
	}
	
	/**
	 * Parses so-called definitions from the source content.
	 * Definitions are named lists or values.
	 * @param lines Lines to process. Bolding and italic effects (*) should already be stripped at this point.
	 * @return All definitions found from the specified lines, with the corresponding lines for each.
	 *
	 *         The definitions (keys) are lower-cased, and only contain the included letters.
	 *         Each included definition contains at least one line entry.
	 *         The string that directly follows the definition key is included as the first line,
	 *         if present / non-empty. The line values are not trimmed.
	 */
	private def parseDefinitions(lines: IterableOnce[String]) = {
		// Collects all encountered definitions into a builder
		val definitionsBuilder = OptimizedIndexedSeq.newBuilder[(String, Seq[String])]
		// If no key is included in the first definition, it is considered a description entry
		var openDefinition = "description"
		val definitionLinesBuilder = OptimizedIndexedSeq.newBuilder[String]
		
		// Iterates the input lines
		lines.iterator.foreach { line =>
			// Case: List item => Adds it to the current definition
			if (newListItemR(line) || indentedLineR(line))
				definitionLinesBuilder += line
			else {
				val splitIndex = line.indexOf(':')
				// Case: This line starts a definition => Completes the previous definition and starts a new one
				// NB: Also considers single-word lines as definition headers
				if (splitIndex > 0 || !line.contains(' ')) {
					val lastDefinitionLines = definitionLinesBuilder.result()
					definitionLinesBuilder.clear()
					
					if (lastDefinitionLines.exists { _.nonEmpty })
						definitionsBuilder += (openDefinition -> lastDefinitionLines.dropRightWhile { _.isEmpty })
					
					val (key, value) = if (splitIndex >= 0) line.splitAt(splitIndex) else (line, "")
					openDefinition = key.filter { _.isLetter }.toLowerCase
					definitionLinesBuilder ++= value.dropWhile { _ == ' ' }.ifNotEmpty
				}
				// Case: Normal content line => Adds it to the current definition
				else
					definitionLinesBuilder += line
			}
		}
		
		// Appends the last definition
		val lastDefinitionLines = definitionLinesBuilder.result()
		if (lastDefinitionLines.exists { _.nonEmpty })
			definitionsBuilder += (openDefinition -> lastDefinitionLines.dropRightWhile { _.isEmpty })
		
		definitionsBuilder.result()
	}
	
	private def parseList(lines: IterableOnce[String]): Seq[ValueTree[String]] = parseList(PollingIterator.from(lines))
	private def parseList(linesIter: PollingIterator[String]) = {
		if (linesIter.hasNext) {
			// Collects all lines that belong to this list
			val lines = linesIter
				.collectUntil { line =>
					// Case: Empty line => Terminates this list
					if (emptyLineR(line))
						true
					else {
						val firstChar = line.head
						// Case: Indented line => Assumes that it continues an item => Continues this list
						if (firstChar == ' ' || firstChar == '\t')
							false
						else
							newListItemR(line)
					}
				}
				// Counts the indentation of each line and checks where new line items are started
				.map { line =>
					// Counts the indentation
					val charsIter = line.iterator
					var indentation = 0
					var indentationEnded = false
					while (!indentationEnded && charsIter.hasNext) {
						charsIter.next() match {
							case ' ' => indentation += 1
							case '\t' => indentation += 2
							case _ => indentationEnded = true
						}
					}
					
					// Checks whether a new line item is started.
					// Removes the marker if present.
					val (lineText, startsListItem) = removeListMarkerFrom(line.trim)
					
					(indentation, lineText, startsListItem)
				}
			
			// Merges lines that represent the same list item
			val itemsBuilder = OptimizedIndexedSeq.newBuilder[(Int, String)]
			var openIndent = 0
			var openItem = ""
			lines.foreach { case (indentation, line, startsListItem) =>
				// Case: A new list item starts => Completes the previous item and starts a new one
				if (startsListItem) {
					if (openItem.nonEmpty)
						itemsBuilder += (openIndent -> openItem)
					
					openIndent = indentation
					openItem = line
				}
				// Case: A continuation line => Appends the currently open value
				else if (openItem.nonEmpty)
					openItem = s"$openItem \n$line"
				// Case: Continuation to an empty line (unexpected) => Appends to the item anyway, but skips the line break
				else
					openItem = line
			}
			// Adds the last property detail if any
			if (openItem.nonEmpty)
				itemsBuilder += (openIndent -> openItem)
			
			// Finally, converts the collected items into a tree
			val items = itemsBuilder.result()
			if (items.isEmpty)
				Empty
			else {
				// The first list item determines the root indentation
				val startingIndentation = items.head._1
				val itemsWithIndicesView = items.view.zipWithIndex
				// Finds each root-level item
				itemsWithIndicesView.iterator.filter { _._1._1 == startingIndentation }
					// Converts each root-level item into a tree by scanning the following lines/items
					.map { case (line, index) =>
						ValueTree(line -> index).iterate { case ((parentIndentation, _), index) =>
							// Checks whether this item has any children
							val firstPossibleChildIndex = index + 1
							items.lift(firstPossibleChildIndex).filter { _._1 > parentIndentation } match {
								// Case: At least one child => Finds the remaining children, also
								case Some(firstChild @ (nextIndentation, _)) =>
									// The other children are those items that appear before the next sibling,
									// that have the same indentation as the first child
									Single(firstChild -> firstPossibleChildIndex) ++
										itemsWithIndicesView.drop(firstPossibleChildIndex + 1).iterator
											.takeWhile { _._1._1 > parentIndentation }.filter { _._1._1 == nextIndentation }
									
									// Case: No children
								case None => Empty
							}
						}
					}
					// Removes indentation and index values from the generated trees
					.map { _.mapValues { _._1._2 } }
					.toOptimizedSeq
			}
		}
		else
			Empty
	}
	
	/**
	 * Removes a list marker from the beginning of a line, if present
	 * @param trimmedLine A line that's already been trimmed
	 * @return Returns 2 values:
	 *         1. The line without a list marker
	 *         1. Whether this line started with a list marker
	 */
	private def removeListMarkerFrom(trimmedLine: String) = {
		// Case: Starts with a list marker => Removes it
		if (startsWithListElementMarkerR(trimmedLine)) {
			val firstMarkerChar = trimmedLine.head
			val withoutMarker = {
				if (firstMarkerChar == ' ')
					trimmedLine.tail.trim
				else
					trimmedLine.dropWhile { _.isDigit }.tail.trim
			}
			(withoutMarker, true)
		}
		// Case: No list marker => Yields the string as-is
		else
			(trimmedLine, false)
	}
	
	private def listToString(list: IterableOnce[ValueTree[String]]): String =
		list.iterator.map { listToString(_) }.mkString("\n")
	private def listToString(list: ValueTree[String], indentation: Int = 0): String = {
		val base = s"${ "\t" * indentation }- ${ list.value }"
		if (list.hasChildren)
			s"$base\n${ list.children.iterator.map { listToString(_, indentation + 1) } }"
		else
			base
	}
}
