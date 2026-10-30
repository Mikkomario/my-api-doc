package vf.myapidoc.controller.testing

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.{OptimizedIndexedSeq, Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.MdParser
import vf.myapidoc.model.cached.testing.evaluation._
import vf.myapidoc.model.cached.testing.instruction._
import vf.myapidoc.model.combined.api.DetailedApiNode
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
	import vf.myapidoc.controller.testing.TestParser.CodeRole._
	
	// ATTRIBUTES   --------------------
	
	private val quotationR = {
		val q = Regex.escape('\"')
		q + (!q).withinParentheses.anyTimes + q
	}
	private val arrayR = {
		val closing = Regex.escape(']')
		Regex.escape('[') + (!closing).withinParentheses.anyTimes + closing
	}
	
	
	// OTHER    ------------------------
	
	def apply(path: Path) = MdParser.iterateSections(path) { sectionsIter =>
		???
	}
	
	// NB: Assumes that linesIter doesn't contain empty lines
	private def parseCode(linesIter: Iterator[String], builder: mutable.Growable[TestInstruction])
	                     (implicit doc: Iterable[DetailedApiNode]): Unit =
	{
		val line = linesIter.next()
		def proceedAfter(line: String) = parseCode(Iterator.single(line) ++ linesIter, builder)
		def proceed() = {
			if (linesIter.hasNext)
				parseCode(linesIter, builder)
		}
		
		if (line.startsWith("//")) {
			builder += PrintComment(line)
			proceed()
		}
		else {
			val tokensIter = tokensIteratorFrom(line)
			val firstToken = expectNextTokenOfType(tokensIter, Code, line)
			firstToken.toLowerCase.notEndingWith(":") match {
				case "if" =>
					val condition = parseEval(tokensIter, line)
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					terminator match {
						case Some(nextLine) =>
							if (nextLine.trim == "else") {
								val (elseCode, terminator) = parseIndentedCodeBlock(linesIter, nextLine)
								builder += ExecuteIf(condition, code, Some(elseCode))
								terminator.foreach(proceedAfter)
							}
							else {
								builder += ExecuteIf(condition, code)
								proceedAfter(nextLine)
							}
						case None => builder += ExecuteIf(condition, code)
					}
				case "while" =>
					val condition = parseEval(tokensIter, line)
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					builder += ExecuteWhile(condition, code)
					terminator.foreach(proceedAfter)
				case "assert" => builder += TestAssertion(parseEval(tokensIter, line))
				case "use" => builder += UpdateAuth(parseEval(tokensIter, line))
				case "run" => builder += RunTest(tokensAsCode(tokensIter, line, failIfEmpty = true))
				case "fail" =>
					builder += (if (tokensIter.hasNext) FailTest(parseEval(tokensIter, line)) else FailTest())
				case other =>
					Method.values.find { _.name.toLowerCase == other } match {
						case Some(method) =>
							val target = tokensAsCode(tokensIter, line, failIfEmpty = true)
							if (target.startsWith("/"))
								builder += ExecuteRequest(method, Left(target))
							else {
								val standardizedTarget = target.filter { _.isLetterOrDigit }.toLowerCase
								doc.find { _.name.filter { _.isLetterOrDigit }.toLowerCase == standardizedTarget } match {
									case Some(node) => builder += ExecuteRequest(method, Right(node))
									case None => throw new SyntaxException(line, s"Unknown request target `$target`")
								}
							}
						case None =>
							// TODO: Parse assignment
							???
					}
			}
		}
	}
	
	private def parseIndentedCodeBlock(linesIter: Iterator[String], openingLine: => String)
	                                  (implicit doc: Iterable[DetailedApiNode]): (TestInstruction, Option[String]) =
	{
		val conditionalCodeBuilder = TestInstruction.newBuilder
		val indentedLinesIter = new IndentedLinesIter(linesIter)
		parseCode(indentedLinesIter, conditionalCodeBuilder)
		conditionalCodeBuilder.result() match {
			case Some(code) => code -> indentedLinesIter.terminator
			case None =>
				val suffix = indentedLinesIter.terminator.getOrElse("End of code")
				throw new SyntaxException(openingLine, s"Expected an intended code block, got $suffix")
		}
	}
	
	private def parseEval(tokensIter: Iterator[(String, CodeRole)], line: => String): TestEvaluation = {
		val (firstToken, firstRole) = expectNextToken(tokensIter, line)
		val primary = parseEvalElement(firstToken, firstRole, tokensIter, line)
		val result = {
			if (tokensIter.hasNext) {
				val secondToken = expectNextTokenOfType(tokensIter, Code, line)
				ComparisonOperator(secondToken) match {
					case Some(operator) =>
						val (thirdToken, thirdRole) = expectNextToken(tokensIter, line)
						val secondary = parseEvalElement(thirdToken, thirdRole, tokensIter, line)
						Compare(Pair(primary, secondary), operator)
					
					case None =>
						throw new SyntaxException(line, s"Expected a comparison operator, got $secondToken")
				}
			}
			else
				primary
		}
		// Makes sure the line's been exhausted
		if (tokensIter.hasNext) {
			val (nextToken, nextRole) = tokensIter.next()
			val nextPartStr = nextRole match {
				case Quotation => nextToken.quoted
				case CodeArray => s"[$nextToken]"
				case Code => nextToken
			}
			throw new SyntaxException(line, s"Expected end of line, got $nextPartStr")
		}
		else
			result
	}
	
	// Expects an input of a single token.
	private def parseEvalElement(element: String, role: CodeRole, moreTokensIter: Iterator[(String, CodeRole)],
	                             line: => String): TestEvaluation =
		role match {
			case Quotation => HardCodedValue(element)
			case CodeArray => parseArray(element, line)
			case _ =>
				element.toLowerCase match {
					case "prompt:" =>
						val (firstParam, firstParamRole) = expectNextToken(moreTokensIter, line)
						firstParamRole match {
							case Quotation => PromptForValue(firstParam)
							case Code =>
								if (firstParam ~== "sensitive:") {
									PromptForValue(expectNextTokenOfType(moreTokensIter, Quotation, line),
										sensitive = true)
								}
								else
									throw new IllegalArgumentException(s"Invalid prompt parameter: $firstParam")
							case other => throw new IllegalArgumentException(s"Invalid prompt parameter type: $other")
						}
					case "csv:" => ReadCsv(parseEval(moreTokensIter, line))
					case _ => parseEvalElement(element, line)
				}
		}
	
	private def parseArray(array: String, line: => String): EvaluateArray = {
		val partsBuilder = OptimizedIndexedSeq.newBuilder[TestEvaluation]
		var openPart: Option[Either[String, TestEvaluation]] = None
		
		def finishPart() = {
			partsBuilder ++= openPart.map {
				case Left(part) => parseEvalElement(part.trim, line)
				case Right(value) => value
			}
			openPart = None
		}
		
		partsIteratorFrom(array).foreach { case (part, role) =>
			role match {
				case Quotation =>
					openPart = openPart match {
						case None => Some(Right(HardCodedValue(part)))
						case Some(Left(custom)) => Some(Left(s"$custom\"$part\""))
						case Some(Right(value)) =>
							throw new SyntaxException(line, s"Can't combine $value with a quotation")
					}
				case CodeArray =>
					openPart = openPart match {
						case None => Some(Right(parseArray(part, line)))
						case Some(Left(custom)) => Some(Left(s"$custom[$part]"))
						case Some(Right(value)) =>
							throw new SyntaxException(line, s"Can't combine $value with an array")
					}
				case Code =>
					Regex.comma.divide(part).foreach {
						case Left(normalPart) =>
							openPart = openPart match {
								case None => Some(Left(normalPart))
								case Some(Left(previous)) => Some(Left(s"$previous$normalPart"))
								case Some(Right(value)) => throw new SyntaxException(line, s"Can't extend $value")
							}
						case Right(_) => finishPart()
					}
			}
		}
		finishPart()
		
		EvaluateArray(partsBuilder.result())
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
			case part => ReadValue(part)
		}
		partsIter.foldLeft(primary) { _ get _ }
	}
	
	private def tokensIteratorFrom(code: String): Iterator[(String, CodeRole)] =
		partsIteratorFrom(code).flatMap { case part @ (content, role) =>
			if (role == Code)
				content.splitIterator(Regex.whitespace).filter { _.nonEmpty }.map { _ -> role }
			else
				Single(part)
		}
	private def partsIteratorFrom(code: String): Iterator[(String, CodeRole)] =
		quotationR.divide(code).iterator.flatMap {
			case Left(normalPart) =>
				arrayR.divide(normalPart).iterator.map {
					case Left(normalPart) => normalPart -> Code
					case Right(arrayPart) => arrayPart.slice(1, arrayPart.length - 1) -> CodeArray
				}
			case Right(quotation) => Single(quotation.slice(1, quotation.length - 1) -> Quotation)
		}
		
	private def tokensAsCode(tokensIter: Iterator[(String, CodeRole)], line: => String, failIfEmpty: Boolean = false) = {
		if (failIfEmpty && !tokensIter.hasNext)
			throw new SyntaxException(line, "Unexpected end of line")
		else
			tokensIter
				.map { case (str, role) =>
					if (role == Code) str else throw new SyntaxException(line, s"Unexpected input type $role")
				}
				.mkString(" ")
	}
	
	private def expectNextTokenOfType(tokensIter: Iterator[(String, CodeRole)], expectedType: CodeRole,
	                                  line: => String) =
	{
		val (part, role) = expectNextToken(tokensIter, line)
		if (role == expectedType)
			part
		else
			throw new SyntaxException(line, s"Expected $expectedType, got $role")
	}
	private def expectNextToken(tokensIter: Iterator[(String, CodeRole)], line: => String) = {
		if (tokensIter.hasNext)
			tokensIter.next()
		else
			throw new SyntaxException(line, "Unexpected end of line")
	}
	
	
	// NESTED   -----------------------
	
	private sealed trait CodeRole
	
	private object CodeRole
	{
		case object Quotation extends CodeRole
		
		case object CodeArray extends CodeRole
		
		case object Code extends CodeRole
	}
	
	private class IndentedLinesIter(source: Iterator[String]) extends Iterator[String]
	{
		// ATTRIBUTES   ---------------------
		
		private var _terminator: Option[String] = None
		private var cached: Option[String] = None
		
		
		// COMPUTED -------------------------
		
		/**
		 * @return Line that terminated this iteration (i.e. a line that's no longer indented).
		 *         None if the end of 'source' was reached, or if this iterator still has more elements.
		 */
		def terminator = _terminator
		
		
		// IMPLEMENTED  ---------------------
		
		override def hasNext: Boolean = {
			if (_terminator.isDefined)
				false
			else if (cached.isDefined)
				true
			else {
				val line = source.next()
				if (line.startsWith("\t")) {
					cached = Some(line.tail)
					true
				}
				else if (line.startsWith("  ")) {
					cached = Some(line.drop(2))
					true
				}
				else {
					_terminator = Some(line)
					false
				}
			}
		}
		// Assumes that hasNext has been called
		override def next(): String = cached.get
	}
}
