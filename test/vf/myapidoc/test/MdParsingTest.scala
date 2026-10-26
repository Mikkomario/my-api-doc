package vf.myapidoc.test

import utopia.flow.collection.immutable.OptimizedIndexedSeq
import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.collection.mutable.iterator.PollingIterator
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.StringFrom

/**
 * Tests Markdown parsing
 * @author Mikko Hilpinen
 * @since 26.10.2026, v0.1
 */
object MdParsingTest extends App
{
	import vf.myapidoc.controller.doc.MdParser._
	
	private val testContent = StringFrom.path("data/test-data/md-test.md").get
	
	// Regexes
	{
		val r = regex
		
		assert(r.indentedLine("\t\tTest line"))
		assert(r.indentedLine("\tTest line"))
		assert(r.indentedLine("  Test line"))
		assert(!r.indentedLine("Test line"))
		
		assert(r.emptyLine(""))
		assert(r.emptyLine("\t"))
		assert(r.emptyLine("\t\t"))
		assert(r.emptyLine("  "))
		assert(!r.emptyLine("Test line"))
		
		assert(r.startsWithListElementMarker("- Test line"))
		assert(!r.startsWithListElementMarker("\t- Test line"))
		assert(!r.startsWithListElementMarker("  - Test line"))
		assert(!r.startsWithListElementMarker("Test line"))
		
		assert(r.newListItem("\t- Test line"))
		assert(r.newListItem("  - Test line"))
		assert(r.newListItem("- Test line"))
		assert(!r.newListItem("\tTest line"))
		assert(!r.newListItem("  Test line"))
		assert(!r.newListItem("Test line"))
	}
	
	// Sections
	private val sections = {
		val linesIter = testContent.linesIterator
		assert(linesIter.next().startsWith("# "))
		assert(linesIter.next() == "Contains test content for MarkDown parsing")
		assert(linesIter.next().isEmpty)
		
		var lastHeader = linesIter.next()
		var lastHeaderDepth = 2
		assert(lastHeader.startsWith("## "))
		lastHeader = lastHeader.drop(3)
		
		val sectionsBuilder = OptimizedIndexedSeq.newBuilder[ValueTree[(String, Seq[String])]]
		while (linesIter.hasNext) {
			val (section, stop) = nextSection(lastHeader, linesIter, lastHeaderDepth)
			sectionsBuilder += section
			stop.foreach { case (depth, header) =>
				lastHeader = header
				lastHeaderDepth = depth
			}
		}
		val sections = sectionsBuilder.result()
		
		assert(sections.size == 5, sections.iterator.map { _.value._1 }.mkString("\n"))
		
		val firstSection = sections.head
		assert(firstSection.value._1 == "Header (2)")
		assert(firstSection.value._2.size == 1, firstSection.value._2.mkString("\n"))
		assert(firstSection.children.isEmpty)
		
		// Make sure deep section contains nested sections
		val deepSection = sections(2)
		assert(deepSection.value._1 == "Deep section")
		assert(deepSection.value._2 == Seq("Header 2 content"))
		assert(deepSection.children.size == 2)
		
		val deepChild1 = deepSection.children.head
		assert(deepChild1.value._1 == "Deeper section 1")
		assert(deepChild1.value._2 == Seq("Header 3 content (1)"))
		assert(deepChild1.children.isEmpty)
		
		val deepChild2 = deepSection.children(1)
		assert(deepChild2.value._1 == "Deeper section 2")
		assert(deepChild2.value._2 == Seq("Header 3 content (2)"))
		assert(deepChild2.children.isEmpty)
		
		assert(lastHeader == "Final header")
		
		sections
	}
	
	// removeListMarkerFrom
	{
		assert(removeListMarkerFrom("- A") == ("A", true))
		assert(removeListMarkerFrom("- B") == ("B", true))
		assert(removeListMarkerFrom("1. A") == ("A", true))
		assert(removeListMarkerFrom("2. B") == ("B", true))
		assert(removeListMarkerFrom("3. C") == ("C", true))
		assert(removeListMarkerFrom("- Additional details") == ("Additional details", true))
		assert(removeListMarkerFrom("Short introductory content.") == ("Short introductory content.", false))
		assert(removeListMarkerFrom("Content: A longer piece of text") == ("Content: A longer piece of text", false))
	}
	
	// Definitions parsing (Definitions section)
	{
		val definitionsSection = sections(3)
		assert(definitionsSection.value._1 == "Definitions section")
		
		// As documented, bolding / italic markers (*) should be stripped before parsing definitions
		val defs = definitions(definitionsSection.value._2.map { _.replace("*", "") })
		assert(defs.size == 4)
		
		assert(defs.head._1 == "description")
		assert(defs.head._2 == Seq("Initial definitions content."))
		
		assert(defs(1)._1 == "values")
		assert(defs(1)._2 == Seq("1. A", "2. B", "3. C"))
		
		assert(defs(2)._1 == "content")
		assert(defs(2)._2 == Seq("A longer piece of text that spans ", "multiple lines of text (in the document)."))
		
		assert(defs(3)._1 == "notice")
		assert(defs(3)._2 == Seq("This is the last definition.", "- Additional details"))
	}
	
	// List parsing and listToString (List section)
	{
		val listSection = sections(1)
		assert(listSection.value._1 == "List section")
		
		val listLines = listSection.value._2.dropWhile { !regex.newListItem(_) }
		
		val parsedList = list(listLines)
		assert(parsedList.size == 3)
		
		val itemA = parsedList.head
		assert(itemA.value == "A")
		assert(itemA.children.isEmpty)
		
		val itemB = parsedList(1)
		assert(itemB.value == "B")
		assert(itemB.children.isEmpty)
		
		val itemC = parsedList(2)
		assert(itemC.value == "C")
		assert(itemC.children.size == 2)
		
		val itemC21 = itemC.children.head
		assert(itemC21.value == "C2.1")
		assert(itemC21.children.size == 1)
		
		val itemC211 = itemC21.children.head
		assert(itemC211.value == "C2.1.1")
		assert(itemC211.children.isEmpty)
		
		val itemC22 = itemC.children(1)
		assert(itemC22.value == "C2.2")
		assert(itemC22.children.isEmpty)
		
		// Also tests polling iterator overload
		val parsedFromPolling = list(PollingIterator.from(listLines))
		assert(parsedFromPolling == parsedList)
		
		// listToString
		assert(listToString(itemA) == "- A")
		assert(listToString(itemC) == "- C\n\t- C2.1\n\t\t- C2.1.1\n\t- C2.2")
		
		val fullListString = listToString(parsedList)
		val expectedListString = "- A\n- B\n- C\n\t- C2.1\n\t\t- C2.1.1\n\t- C2.2"
		assert(fullListString == expectedListString, s"Expected:\n$expectedListString\nActual:\n$fullListString")
	}
	
	println("Success!")
}
