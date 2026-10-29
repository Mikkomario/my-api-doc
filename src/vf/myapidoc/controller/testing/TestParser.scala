package vf.myapidoc.controller.testing

import vf.myapidoc.controller.MdParser

import java.nio.file.Path

/**
 * An interface for parsing tests from Markdown files
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
object TestParser
{
	def apply(path: Path) = MdParser.iterateSections(path) { sectionsIter =>
		???
	}
}
