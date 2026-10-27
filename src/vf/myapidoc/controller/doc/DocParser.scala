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
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.UncertainBoolean
import utopia.scribe.core.controller.logging.Scribe
import vf.myapidoc.model.cached._
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.ModelProp
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
	
	private val andR = Regex("AND") || Regex.escape('&').times(2)
	
	
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
			// Removes links, italic & bold effects, as well as trailing whitespaces from all lines
			val formattedLinesIter = linesIter.map(MdParser.stripFormattingFrom)
			val groupsBuilder = OptimizedIndexedSeq.newBuilder[NewApiNodeGroup]
			
			// Finds the first header
			formattedLinesIter.findMap { line =>
				val headerDepth = line.iterator.takeWhile { _ == '#' }.size
				if (headerDepth >= groupHeaderLevel) Some(headerDepth -> line.drop(headerDepth).trim) else None
			} match {
				case Some((firstHeaderDepth, firstHeader)) =>
					// Continually parses the content one (node group) section at a time
					var nextHeaderDepth = firstHeaderDepth
					var nextHeader = firstHeader
					while (formattedLinesIter.hasNext) {
						val (section, stopHeader) = MdParser.nextSection(nextHeader, formattedLinesIter, nextHeaderDepth)
						
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
			// Removes special formatting
			val pollingLinesIter = linesIter.map(MdParser.stripFormattingFrom).pollable
			// NB: Current version assumes that all models are listed at header level 2
			// Correct if necessary
			OptionsIterator.continually { pollingLinesIter.find { _.startsWith("##") } }
				.flatMap { headerLine => processModel(headerLine.dropWhile { _ == '#' }.trim, pollingLinesIter) }
				.toOptimizedSeq
		}
		
	private def processApiNodeFrom(section: ValueTree[(String, Seq[String])]) = {
		val (name, lines) = section.value
		
		// Finds the portion of this section that concerns this API node generally
		val methodLineIndex = lines.findIndexWhere { _.startsWithIgnoreCase("Method:") }
		
		// Extracts and processes the listed definitions
		var parentName = ""
		var path = ""
		var pathParams: Seq[NewProperty] = Empty
		val (description, tokenOptions, scope) = processDefinitions(methodLineIndex match {
			case Some(i) => lines.view.take(i)
			case None => lines
		}) {
			(key, lines) =>
				key match {
					case "parent" =>
						parentName = lines.head
						true
					case "path" =>
						path = lines.head.notSurroundedWith("`").notStartingWith("#parent")
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
					lines(headerIndex).drop(7).splitAtFirst(":").map { _.trim }.toTuple
				val results = parseMethodImplementation(name, methodNamePart, lines.drop(headerIndex + 1))
				
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
			.map { _.trim.notSurroundedWith("`") }.filter { _.forall { _.isLetter } }.map(Method.apply).toOptimizedSeq
		
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
						if (MdParser.regex.newListItem(lines.head)) {
							val bodyOptions = MdParser.list(lines).map(processBodyEntry)
							optionalRequestBody = bodyOptions.exists { _.isEmpty }
							requestBodyOptions = bodyOptions.flatten
						}
						else
							processBodyEntry(ValueTree(lines.head).withChildren(MdParser.list(lines.view.tail))) match {
								case Some(body) => requestBodyOptions = Single(body)
								case None => optionalRequestBody = true
							}
						true
						
					case "requestbodyoptions" =>
						val bodyOptions = MdParser.list(lines).map(processBodyEntry)
						optionalRequestBody = bodyOptions.exists { _.isEmpty }
						requestBodyOptions = bodyOptions.flatten
						true
					case "response" =>
						val responseDetails = MdParser.list(lines)
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
											val extraDetails = MdParser.listToString(statusNode.children)
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
		val definitions = MdParser.definitions(lines)
		var description = ""
		var tokenOptions: Seq[(String, String)] = Empty
		var scope: Seq[(Seq[String], String)] = Empty
		definitions.foreach { case (key, lines) =>
			key match {
				case "scope" | "scopeoptions" =>
					if (scope.isEmpty) {
						// Checks whether the scopes are listed as list items or as a single entry
						val firstListItemIndex = lines.indexWhere(MdParser.regex.newListItem.apply)
						// Case: Listed scopes => Parses multiple scopes
						if (firstListItemIndex == 0)
							scope = MdParser.list(lines).map(parseScope)
						// Case: Single scope entry => Parses a single scope
						else {
							// Checks whether details are included as list items
							val (primaryEntry, listDetails) = {
								if (firstListItemIndex < 0)
									lines.mkString("\n") -> Empty
								else {
									val (primaryEntryLines, listLines) = lines.splitAt(firstListItemIndex)
									primaryEntryLines.mkString("\n") -> MdParser.list(listLines)
								}
							}
							scope = Single(parseScope(ValueTree(primaryEntry).withChildren(listDetails)))
						}
					}
					// Case: Duplicate scope entry => Logs and ignores
					else
						scribe.in("process.apiNode.definition.scope.duplicate")
							.recoverable("Scope was defined multiple times", loggingContext)
				case "authorization" | "auth" | "authorizationoptions" | "authoptions" =>
					if (tokenOptions.isEmpty)
						tokenOptions = parseTokenTypes(lines)
					else
						scribe.in("process.apiNode.definition.auth.duplicate")
							.recoverable("Authorization options were defined multiple times", loggingContext)
				case "description" =>
					description = s"${ description.appendIfNotEmpty("\n\n") }${ lines.mkString(" \n") }"
				case other =>
					if (!processOther(other, lines)) {
						val header = other.capitalize
						val addition = {
							if (lines.headOption.exists(MdParser.regex.newListItem.apply))
								s"$header:\n${ lines.mkString("\n") }"
							else
								s"$header: ${ lines.mkString("\n") }"
						}
						description = {
							if (description.isEmpty)
								addition
							else
								s"$description\n\n$addition"
						}
					}
			}
		}
		(description, tokenOptions, scope)
	}
	
	private def processBodyEntry(entry: ValueTree[String]) = {
		val key = MdParser.removeListMarkerFrom(entry.value.trim)._1
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
			// Processes the body content type
			val (dataType, properties, referencedType, details) = {
				// Case: Unnamed object => Parses the listed properties
				if (mainKeyPart.startsWithIgnoreCase("Object"))
					(ModelProp, entry.children.map(processProp), "", "")
				// Case: Basic type or named object reference
				//       => Keeps the reference and includes possible list elements as details
				else {
					val details = MdParser.listToString(entry.children)
					PropertyType.findForName(mainKeyPart) match {
						case Some(basicType) => (basicType, Empty, "", details)
						case None => (ModelProp, Empty, mainKeyPart, details)
					}
				}
			}
			Some(NewBodyDefinition(dataType = dataType, properties = properties, referencedType = referencedType,
				contentType = customContentType, details = details, isArray = isArray))
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
	
	private def parseProps(linesIter: PollingIterator[String]): Seq[NewProperty] =
		MdParser.list(linesIter).map(processProp)
	private def processProp(propInput: ValueTree[String]): NewProperty = {
		val propName = propInput.value
		
		// Collects information from the nested details
		val typesBuilder = OptimizedIndexedSeq.newBuilder[PropertyType]
		var referencedType = ""
		var optional = false
		var array: UncertainBoolean = false
		val descriptionBuilder = OptimizedIndexedSeq.newBuilder[ValueTree[String]]
		var children: Seq[NewProperty] = Empty
		var possibleValues: Seq[(String, String)] = Empty
		
		propInput.children.foreach { detail =>
			// Checks for key-value syntax
			val parts = detail.value.splitAtFirst(":").map { _.trim }
			val value = parts.second
			parts.first.filter { _.isLetter }.toLowerCase match {
				case "type" =>
					// Checks for multiple alternative types
					val parts = value.split('|').iterator
						.map { entry =>
							val trimmed = entry.trim
							val (content, isArray) = {
								// Case: Array -prefix => Accepts an array of values.
								//                        Suffix determines the accepted type.
								if (trimmed.startsWithIgnoreCase("Array"))
									trimmed.afterFirst(":") -> true
								else
									trimmed -> false
							}
							// Checks whether a basic type is referenced
							PropertyType.findForName(content).toRight(content) -> isArray
						}
						.toOptimizedSeq
					// Checks whether an array is expected, and whether that's true 100% of the time
					val arrayElements = parts.count { _._2 }
					if (arrayElements > 0) {
						if (arrayElements == parts.size)
							array = true
						else
							array = UncertainBoolean
					}
					// If a non-basic type was referenced, we'll store it as a referenced (model) type
					if (referencedType.isEmpty)
						parts.findMap { _._1.leftOption }.foreach { referencedType = _ }
					// Remembers the referenced basic types
					typesBuilder ++= parts.iterator.flatMap { _._1.rightOption }
					
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
								val description = Pair(parts.second, MdParser.listToString(possibleValue.children))
									.filter { _.nonEmpty }.mkString("\n")
								parts.first -> description
							}
							.toOptimizedSeq
					
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
			description = descriptionBuilder.result().emptyOneOrMany match {
				case None => ""
				case Some(Left(onlyEntry)) =>
					Pair(onlyEntry.value, MdParser.listToString(onlyEntry.children)).filter { _.nonEmpty }.mkString("\n")
				case Some(Right(entries)) => MdParser.listToString(entries)
			},
			children = children,
			possibleValues = possibleValues, isOptional = optional, isArray = array)
	}
	
	private def parseTokenTypes(lines: Iterable[String]) = {
		// Checks whether token type/types are introduced as a list or as direct values
		val firstLine = lines.head
		val tokenEntries = {
			// Case: List of values => Extracts each list item
			if (MdParser.regex.newListItem(firstLine))
				MdParser.list(lines).map { entry => entry.value -> entry.children }
			// Case: Direct value => Expects the value to represent a single token type.
			//                       Also extracts the following list items (as details), if applicable.
			else
				Single(lines.head -> MdParser.list(lines.view.tail))
		}
		// Separates the token type names from their descriptions
		tokenEntries.map { case (mainEntry, details) =>
			val mainEntryParts = mainEntry.splitAtFirst(":").map { _.trim }
			mainEntryParts.first ->
				Pair(mainEntryParts.second, MdParser.listToString(details)).filter { _.nonEmpty }.mkString("\n")
		}
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
				MdParser.listToString(scopeEntry.children)
			else
				""
		}
		// Checks whether multiple scopes are joined
		val parts = scopeEntry.value.split(andR)
		// Case: Multiple scopes are required => Parses each part, expecting no description to be included
		if (parts.size > 1)
			parts.map { _.notSurroundedWith("(", ")").notSurroundedWith("`") } -> nestedDescription
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
}
