package vf.myapidoc.controller.testing

import utopia.access.model.enumeration.Method
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.MdParser
import vf.myapidoc.model.cached.testing.evaluation.{HardCodedValue, LastResponseValue, PromptForValue, ReadCsv, ReadValue, TestEvaluation}
import vf.myapidoc.model.cached.testing.instruction.{PrintComment, TestInstruction}
import vf.myapidoc.model.error.SyntaxException

import java.nio.file.Path
import scala.collection.mutable

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
	
	// NB: Assumes that linesIter doesn't contain empty lines
	private def parseCode(linesIter: Iterator[String], builder: mutable.Growable[TestInstruction]): Unit = {
		val line = linesIter.next()
		def proceed() = {
			if (linesIter.hasNext)
				parseCode(linesIter, builder)
		}
		
		if (line.startsWith("//")) {
			builder += PrintComment(line)
			proceed()
		}
		else {
			// TODO: Don't split within quotations
			val tokensIter = line.splitIterator(Regex.whitespace)
			tokensIter.next().notEndingWith(":") match {
				case "if" => ???
				case "while" =>
				case "assert" => ???
				case "use" => ???
				case "run" => ???
				case "fail" => ???
				case other =>
					Method.values.find { _.name.toLowerCase == other } match {
						case Some(method) => ???
						case None =>
							???
					}
			}
		}
	}
	
	// TODO: Add handling for arrays
	private def parseEval(tokensIter: Iterator[String], line: => String): TestEvaluation = {
		if (tokensIter.hasNext)
			tokensIter.next() match {
				case "prompt:" =>
					val remainder = tokensIter.mkString(" ")
					if (remainder.startsWith("sensitive:"))
						PromptForValue(remainder.drop(10).trim, sensitive = true)
					else
						PromptForValue(remainder)
				case "csv:" => ReadCsv(parseEval(tokensIter, line))
				case primary =>
					
					
					???
			}
		else
			throw new SyntaxException(line, s"Expected expression on line `$line`")
	}
	
	private def parseEvalElement(element: String, line: => String): TestEvaluation = {
		val partsIter = element.splitIterator(Regex.dot)
		def nextPart() = {
			if (partsIter.hasNext)
				partsIter.next()
			else
				throw new SyntaxException(line, s"Expected element on line `$line`")
		}
		val primary: TestEvaluation = nextPart() match {
			case "last" => LastResponseValue(nextPart())
			case "null" => HardCodedValue.empty
			case "true" => HardCodedValue.trueValue
			case "false" => HardCodedValue.falseValue
			case part =>
				if (part.startsWith("\""))
					???
				else
					ReadValue(part)
		}
		partsIter.foldLeft(primary) { _ get _ }
	}
}
