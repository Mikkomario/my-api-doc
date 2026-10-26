package vf.myapidoc.controller.doc

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, OptimizedIndexedSeq, Single}
import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.collection.mutable.iterator.PollingIterator
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._

/**
 * Provides functions for parsing Markdown documents
 * @author Mikko Hilpinen
 * @since 26.10.2026, v0.1
 */
object MdParser
{
	// COMPUTED --------------------
	
	/**
	 * @return Access to regular expressions related to Markdown documents
	 */
	def regex = MdExpressions
	
	
	// OTHER -----------------------
	
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
	def nextSection(header: String, linesIter: Iterator[String],
	                currentDepth: Int = 1): (ValueTree[(String, Seq[String])], Option[(Int, String)]) =
	{
		// Reads lines until the targeted section ends or until the end of the document is reached
		val linesBuilder = OptimizedIndexedSeq.newBuilder[String]
		val subsectionsBuilder = OptimizedIndexedSeq.newBuilder[ValueTree[(String, Seq[String])]]
		var stop: Option[(Int, String)] = None
		while (stop.isEmpty && linesIter.hasNext) {
			val line = linesIter.next()
			val headerDepth = line.iterator.takeWhile { _ == '#' }.size
			// Case: Normal content row => Adds it to the current section
			if (headerDepth == 0)
				linesBuilder += line
			// Case: Section ends => Returns
			else if (headerDepth <= currentDepth)
				stop = Some((headerDepth, line.drop(headerDepth).trim))
			// Case: A subsection starts => Reads it using recursion
			else {
				// Continues reading the subsections until the current (main) section ends
				// or the end of the document is reached
				var nextChildDepth = headerDepth
				var nextChildHeader = line.drop(headerDepth).trim
				while (stop.isEmpty && linesIter.hasNext) {
					val (subsection, childStop) = nextSection(header = nextChildHeader,
						linesIter = linesIter, currentDepth = nextChildDepth)
					subsectionsBuilder += subsection
					
					childStop.foreach { case (nextSectionDepth, nextSectionHeader) =>
						if (nextSectionDepth <= currentDepth)
							stop = childStop
						else {
							nextChildDepth = nextSectionDepth
							nextChildHeader = nextSectionHeader
						}
					}
				}
			}
		}
		
		// Returns the parsed section and the stopping header
		ValueTree[(String, Seq[String])](header -> linesBuilder.result().dropRightWhile { _.isEmpty })
			.withChildren(subsectionsBuilder.result()) -> stop
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
	def definitions(lines: IterableOnce[String]) = {
		// Collects all encountered definitions into a builder
		val definitionsBuilder = OptimizedIndexedSeq.newBuilder[(String, Seq[String])]
		// If no key is included in the first definition, it is considered a description entry
		var openDefinition = "description"
		val definitionLinesBuilder = OptimizedIndexedSeq.newBuilder[String]
		
		// Iterates the input lines
		lines.iterator.foreach { line =>
			// Case: List item => Adds it to the current definition
			if (regex.newListItem(line) || regex.indentedLine(line))
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
	
	/**
	 * Parses a single list of items
	 * @param lines Lines that form this list.
	 *              May also yield other content, but parsing is terminated when the list content ends.
	 * @return Parsed list elements as trees
	 */
	def list(lines: IterableOnce[String]): Seq[ValueTree[String]] = list(PollingIterator.from(lines))
	/**
	 * Parses a single list of items
	 * @param linesIter An iterator that yields list content.
	 *                  May also yield other content, but parsing is terminated when the list content ends.
	 * @return Parsed list elements as trees
	 */
	def list(linesIter: PollingIterator[String]) = {
		if (linesIter.hasNext) {
			// Collects all lines that belong to this list
			val lines = linesIter
				.collectUntil { line =>
					// Case: Empty line => Terminates this list
					if (regex.emptyLine(line))
						true
					else {
						val firstChar = line.head
						// Case: Indented line => Assumes that it continues an item => Continues this list
						if (firstChar == ' ' || firstChar == '\t')
							false
						else
							regex.newListItem(line)
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
	def removeListMarkerFrom(trimmedLine: String) = {
		// Case: Starts with a list marker => Removes it
		if (regex.startsWithListElementMarker(trimmedLine)) {
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
	
	/**
	 * Converts a parsed MD list to a string
	 * @param list Parsed list elements
	 * @return A string representation of the specified list
	 */
	def listToString(list: IterableOnce[ValueTree[String]]): String =
		list.iterator.map { listToString(_) }.mkString("\n")
	/**
	 * Converts a parsed MD list element to a string
	 * @param list Parsed list element
	 * @param indentation Indentation to apply for this element. Default = 0.
	 * @return A string representation of the specified list
	 */
	def listToString(list: ValueTree[String], indentation: Int = 0): String = {
		val base = s"${ "\t" * indentation }- ${ list.value }"
		if (list.hasChildren)
			s"$base\n${ list.children.iterator.map { listToString(_, indentation + 1) } }"
		else
			base
	}
	
	
	// NESTED   ---------------------------
	
	object MdExpressions
	{
		private val indentation = Regex.whitespace || Regex.escape('\t')
		/**
		 * Matches a line that has one or more indent.
		 */
		val indentedLine = indentation.withinParentheses + Regex.any
		
		/**
		 * Matches an empty line
		 */
		val emptyLine = indentation.withinParentheses.anyTimes
		private val listElementMarker = Regex.dash || (Regex.digit.oneOrMoreTimes + Regex.dot).withinParentheses
		/**
		 * Matches strings that start with a list element.
		 * Expects no indentation.
		 */
		val startsWithListElementMarker = listElementMarker.withinParentheses + Regex.any
		/**
		 * Matches lines that start new list elements.
		 * Supports indentation.
		 */
		val newListItem = indentation.withinParentheses.anyTimes + startsWithListElementMarker.withinParentheses
	}
}
