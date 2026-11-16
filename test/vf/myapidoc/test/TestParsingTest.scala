package vf.myapidoc.test

import utopia.flow.collection.immutable.Empty
import vf.myapidoc.controller.testing.TestParser
import vf.myapidoc.model.combined.api.DetailedApiNode

/**
 * Tests a few test parsing cases
 * @author Mikko Hilpinen
 * @since 15.11.2026, v0.1
 */
object TestParsingTest extends App
{
	private implicit val nodes: Seq[DetailedApiNode] = Empty
	
	private val testCode = "if adminKey == null\n  adminKey = prompt: \"Enter the admin API key\"\n  persist adminKey\n  if adminKey == null\n    FAIL \"No admin API key was specified => Can't continue testing\"\nuse adminKey"
	private val code = TestParser.parseCode("TEST", testCode.linesIterator).get
	
	println("Parsing succeeded!")
}
