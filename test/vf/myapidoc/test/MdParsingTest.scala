package vf.myapidoc.test

import utopia.flow.collection.immutable.OptimizedIndexedSeq
import utopia.flow.collection.immutable.tree.ValueTree
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
		
		// TODO: Make sure deep section contains nested sections
		
		assert(lastHeader == "Final header")
		
		sections
	}
	
	// TODO: Test removeListMarkerFrom
	// TODO: Test definitions parsing (Definitions section)
	// TODO: Test list parsing and listToString (List section)
	
	println("Success!")
}
