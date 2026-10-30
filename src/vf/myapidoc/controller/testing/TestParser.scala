package vf.myapidoc.controller.testing

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{OptimizedIndexedSeq, Pair, Single}
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.MdParser
import vf.myapidoc.model.cached.testing.Test
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
	private val arrayR = Regex.escape('[') + Regex.any.anyTimes + Regex.escape(']')
	
	
	// OTHER    ------------------------
	
	/**
	 * Parses all tests from a specific Markdown document.
	 *
	 * Expects the document to contain named test sections at level 2 or below.
	 * Each of these sections should:
	 *      1. Contain no subsections
	 *      1. Contain a code block
	 *
	 * @param path Path to the document to read
	 * @param doc Implicit API node documentation
	 * @return Parsed tests. Failure if file-reading failed or test-compilation failed (malformed syntax).
	 */
	// Parses the sections
	def apply(path: Path)(implicit doc: Iterable[DetailedApiNode]) =
		MdParser.iterateSections(path) { sectionsIter =>
			// Only targets the lowest level sections
			sectionsIter.flatMap { _.leavesIterator }
				.flatMap { section =>
					val (name, lines) = section.value
					// Looks for a code block. If not found, ignores this section.
					lines.findIndexWhere { _.startsWith("```") }.flatMap { codeBlockIndex =>
						try {
							// Attempts to compile the code
							val codeBuilder = TestInstruction.newBuilder
							parseCode(
								lines.view.drop(codeBlockIndex + 1).iterator.takeWhile { !_.startsWith("```") }
									// Ignores empty code lines
									.filter { _.nonEmpty },
								codeBuilder)
							codeBuilder.result().map { code =>
								// Case: Compilation succeeded => Prepares the test
								Test(name, code,
									description = lines.view.take(codeBlockIndex).dropRightWhile { _.isEmpty }
										.mkString("\n"))
							}
						}
						catch {
							// Case: Compilation failed => Throws, including the name of this test
							case e: SyntaxException =>
								throw new SyntaxException(e.line, s"Failed to compile test $name", e)
						}
					}
				}
				.toOptimizedSeq
		}
	
	/**
	 * Parses a single code block
	 * @param linesIter An iterator that yields the code lines.
	 *                  Must fulfill the following conditions:
	 *                  - The first line is NOT indented
	 *                  - No empty lines are included
	 * @param builder A builder for the resulting code
	 * @param doc Implicit API nodes documentation
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseCode(linesIter: Iterator[String], builder: mutable.Growable[TestInstruction])
	                     (implicit doc: Iterable[DetailedApiNode]): Unit =
	{
		// Attempts to parse the next code line
		val line = linesIter.next()
		def proceedAfter(line: String) = parseCode(Iterator.single(line) ++ linesIter, builder)
		
		// Case: Comment line => Adds code that prints that comment to test logs
		if (line.startsWith("//"))
			builder += PrintComment(line)
		else {
			// Splits the line into tokens
			val tokensIter = tokensIteratorFrom(line)
			// Expects the line to start with a code token (a keyword or a variable reference)
			val firstToken = expectNextTokenOfType(tokensIter, Code, line)
			firstToken.toLowerCase.notEndingWith(":") match {
				// Case: If statement => Parses the condition and the conditional code block
				case "if" =>
					val condition = parseEval(tokensIter, line)
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					terminator match {
						// Case: Code continues after the if block => Checks whether an else block follows
						case Some(nextLine) =>
							// Case: Else block follows
							//       => Parses the else code block and it to the if sequence
							if (nextLine.trim == "else") {
								val (elseCode, terminator) = parseIndentedCodeBlock(linesIter, nextLine)
								builder += ExecuteIf(condition, code, Some(elseCode))
								terminator.foreach(proceedAfter)
							}
							// Case: No else block => Finishes the if section and continues parsing
							else {
								builder += ExecuteIf(condition, code)
								proceedAfter(nextLine)
							}
						// Case: End of code => Finishes the if section
						case None => builder += ExecuteIf(condition, code)
					}
				// Case: While statement => Parses the condition and the repeated code block
				case "while" =>
					val condition = parseEval(tokensIter, line)
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					builder += ExecuteWhile(condition, code)
					terminator.foreach(proceedAfter)
				// Case: Assertion => Parses the asserted expression
				case "assert" => builder += TestAssertion(parseEval(tokensIter, line))
				// Case: Use authorization statement => Parses an authorization change instruction
				case "use" => builder += parseAuthInstruction(tokensIter, line)
				// Case: Run statement => Parses the code to run another test
				case "run" => builder += RunTest(tokensAsCode(tokensIter, line))
				// Case: FAIL statement => Parses the code to fail the test
				case "fail" =>
					// The remaining part, if present, is included as a failure message
					builder +=
						(if (tokensIter.hasNext) FailTest(getMessage = parseEval(tokensIter, line)) else FailTest())
				case other =>
					// Checks whether the line starts with a method name
					Method.values.find { _.name.toLowerCase == other } match {
						// Case: Method prefix => Parses code for executing a HTTP request
						case Some(method) =>
							// Parses the request target
							val target = tokensAsCode(tokensIter, line)
							// Case: Target starts with "/" => Uses a custom path
							if (target.startsWith("/"))
								builder += ExecuteRequest(method, Left(target))
							// Case: Not starting with "/" => Expects an API node name reference
							else {
								val standardizedTarget = target.filter { _.isLetterOrDigit }.toLowerCase
								doc.find { _.name.filter { _.isLetterOrDigit }.toLowerCase == standardizedTarget } match {
									// Case: API node found => Adds a request to that node
									case Some(node) => builder += ExecuteRequest(method, Right(node))
									// Case: No API node found => Compilation fails
									case None => throw new SyntaxException(line, s"Unknown request target `$target`")
								}
							}
						case None =>
							// Expects an assignment
							val secondToken = expectNextTokenOfType(tokensIter, Code, line)
							// Case: Assignment found => Parses the assigned value and adds an assignment
							if (secondToken == "=") {
								val value = parseEval(tokensIter, line)
								builder += (firstToken.split(Regex.dot).oneOrMany match {
									case Left(key) => Assign(key, value)
									case Right(path) => AssignPropertyValue(path, value)
								})
							}
							// Case: No assignment found (either) => Fails compilation
							else
								throw new SyntaxException(line,
									s"Expected `=` after `$firstToken`, but got `$secondToken`")
					}
			}
		}
		// Continues parsing, if appropriate
		if (linesIter.hasNext)
			parseCode(linesIter, builder)
	}
	
	/**
	 * Parses a code block from sequential indented code lines
	 * @param linesIter An iterator that yields the remaining code lines.
	 * @param openingLine Line that appears right before this block. Used in error logging.
	 * @param doc Implicit API nodes documentation
	 * @return Returns two values:
	 *         1. Parsed code block
	 *         1. Next line, or None if the end of 'linesIter' was reached.
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseIndentedCodeBlock(linesIter: Iterator[String], openingLine: => String)
	                                  (implicit doc: Iterable[DetailedApiNode]): (TestInstruction, Option[String]) =
	{
		val conditionalCodeBuilder = TestInstruction.newBuilder
		val indentedLinesIter = new IndentedLinesIter(linesIter)
		parseCode(indentedLinesIter, conditionalCodeBuilder)
		conditionalCodeBuilder.result() match {
			case Some(code) => code -> indentedLinesIter.terminator
			// Case: No code block found => Fails compilation
			case None =>
				val suffix = indentedLinesIter.terminator.getOrElse("End of code")
				throw new SyntaxException(openingLine, s"Expected an intended code block, got $suffix")
		}
	}
	
	private def parseAuthInstruction(tokensIter: Iterator[(String, CodeRole)], line: => String) = {
		val remainingTokens = tokensIter.toOptimizedSeq
		// Looks for a code token involving "+"
		remainingTokens.iterator.zipWithIndex.findMap { case ((token, role), index) =>
			if (role == Code) {
				// Case: + token => Splits the authorization at that token
				if (token == "+")
					Some(Pair(remainingTokens.take(index), remainingTokens.drop(index + 1)))
				// Case: A token including "+" => Splits the authorization at "+"
				else if (token.contains('+')) {
					val parts = token.splitAtFirst("+")
					val left = {
						val prefix = remainingTokens.take(index)
						if (parts.first.isEmpty) prefix else prefix :+ (parts.first -> Code)
					}
					val right = {
						val suffix = remainingTokens.drop(index + 1)
						if (parts.second.isEmpty) suffix else (parts.second -> Code) +: suffix
					}
					Some(Pair(left, right))
				}
				else
					None
			}
			else
				None
		} match {
			// Case: Basic auth parts found => Applies basic authorization
			case Some(basicAuthParts) =>
				UpdateBasicAuth(basicAuthParts.map { tokens => parseEval(tokens.iterator, line) })
			// Case: No + token found => Applies bearer token authorization
			case None => UpdateAuth(parseEval(tokensIter, line))
		}
	}
	
	/**
	 * Parses the line remainder into an evaluation / expression
	 * @param tokensIter An iterator that yields the remaining tokens on this line
	 * @param line Complete line. Used in error logging.
	 * @return Parsed evaluation
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseEval(tokensIter: Iterator[(String, CodeRole)], line: => String): TestEvaluation = {
		// Parses the first token / element
		val (firstToken, firstRole) = expectNextToken(tokensIter, line)
		val primary = parseEvalElement(firstToken, firstRole, tokensIter, line)
		val result = {
			// Case: Not the end of the line => Expects a comparison
			if (tokensIter.hasNext) {
				val secondToken = expectNextTokenOfType(tokensIter, Code, line)
				ComparisonOperator(secondToken) match {
					// Case: Comparison found => Parses the secondary element
					case Some(operator) =>
						val (thirdToken, thirdRole) = expectNextToken(tokensIter, line)
						val secondary = parseEvalElement(thirdToken, thirdRole, tokensIter, line)
						Compare(Pair(primary, secondary), operator)
					
					// Case: No comparison found => Fails compilation
					case None =>
						throw new SyntaxException(line, s"Expected a comparison operator, got $secondToken")
				}
			}
			else
				primary
		}
		// Makes sure the line's been exhausted
		// Case: Line was not fully exhausted => Fails compilation
		if (tokensIter.hasNext) {
			val (nextToken, nextRole) = tokensIter.next()
			val nextPartStr = nextRole match {
				case Quotation => nextToken.quoted
				case CodeArray => s"[$nextToken]"
				case Code => nextToken
			}
			throw new SyntaxException(line, s"Expected end of line, got $nextPartStr")
		}
		// Case: End of line reached => Returns the parsed evaluation
		else
			result
	}
	
	/**
	 * Parses a line token into an evaluation / expression
	 * @param element Element to parse
	 * @param role Type of this element
	 * @param moreTokensIter An iterator that yields additional tokens. Used for multi-token elements.
	 * @param line Line being parsed. Used in error logging.
	 * @return Parsed evaluation.
	 */
	@throws[SyntaxException]("If parsing failed")
	// Expects an input of a single token.
	private def parseEvalElement(element: String, role: CodeRole, moreTokensIter: Iterator[(String, CodeRole)],
	                             line: => String): TestEvaluation =
		role match {
			// Case: Quotation => Yields a hard-coded string value
			case Quotation => HardCodedValue.string(element)
			// Case: Array => Yields an array of evaluations / expressions
			case CodeArray => parseArray(element, line)
			// Case: Code token => Checks for custom commands
			case _ =>
				element.toLowerCase match {
					// Case: Prompt command => Requests a value from the user when executed
					case "prompt:" =>
						// Expects a prompt expression to follow
						val (firstParam, firstParamRole) = expectNextToken(moreTokensIter, line)
						firstParamRole match {
							// Case: Prompt as a quotation
							case Quotation => PromptForValue(firstParam)
							// Case: A code token follows => Expects a "sensitive:" -parameter
							case Code =>
								// Case: Sensitive parameter found => Expects a quotation
								if (firstParam ~== "sensitive:")
									PromptForValue(expectNextTokenOfType(moreTokensIter, Quotation, line), hide = true)
								// Case: Unsupported parameter type => Fails
								else
									throw new IllegalArgumentException(s"Invalid prompt parameter: $firstParam")
							case other => throw new IllegalArgumentException(s"Invalid prompt parameter type: $other")
						}
					// Case: CSV command => Reads CSV based on the following expression
					case "csv:" => ReadCsv(parseEval(moreTokensIter, line))
					// Case: No custom command => Parses the element as a normal evaluation
					case _ => parseEvalElement(element, line)
				}
		}
	
	/**
	 * Parses an array of evaluations / expressions from code
	 * @param array The code that represents an array
	 * @param line Line at which this array was found. Used in error logging.
	 * @return Parsed array code
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseArray(array: String, line: => String): EvaluateArray = {
		// Builds the array in parts
		val partsBuilder = OptimizedIndexedSeq.newBuilder[TestEvaluation]
		var openPart: Option[Either[String, TestEvaluation]] = None
		
		// Completes the currently open part, if applicable
		def finishPart() = {
			partsBuilder ++= openPart.map {
				// Case: Custom code part => Parses the custom code as an evaluation
				case Left(part) => parseEvalElement(part.trim, line)
				// Case: Preprocessed evaluation => Adds it to the array
				case Right(value) => value
			}
			openPart = None
		}
		
		// Handles quotations and nested arrays separately
		partsIteratorFrom(array).foreach { case (part, role) =>
			role match {
				// Case: Quotation found => Adds it as the open part (as a hard-coded string value)
				case Quotation =>
					openPart = openPart match {
						case None =>
							import utopia.flow.generic.casting.ValueConversions._
							Some(Right(HardCodedValue(part)))
						case Some(Left(custom)) => Some(Left(s"$custom\"$part\""))
						case Some(Right(value)) =>
							throw new SyntaxException(line, s"Can't combine $value with a quotation")
					}
				// Case: Nested array found => Parses it and adds it as the open part
				case CodeArray =>
					openPart = openPart match {
						case None => Some(Right(parseArray(part, line)))
						case Some(Left(custom)) => Some(Left(s"$custom[$part]"))
						case Some(Right(value)) =>
							throw new SyntaxException(line, s"Can't combine $value with an array")
					}
				// Case: Other content => Splits it into array elements
				case Code =>
					// Iterates over the elements
					Regex.comma.divide(part).foreach {
						// Case: Array element => Adds it as the open part
						case Left(normalPart) =>
							openPart = openPart match {
								case None => Some(Left(normalPart))
								case Some(Left(previous)) => Some(Left(s"$previous$normalPart"))
								case Some(Right(value)) => throw new SyntaxException(line, s"Can't extend $value")
							}
						// Case: Comma => Finishes the currently open part
						case Right(_) => finishPart()
					}
			}
		}
		// Adds the last array element, if applicable
		finishPart()
		
		// Yields the completed array
		EvaluateArray(partsBuilder.result())
	}
	
	/**
	 * Converts a single code token into an evaluation / expression
	 * @param element Element to parse
	 * @param line Line on which this element appears. Used in error logging.
	 * @return Parsed evaluation / expression
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseEvalElement(element: String, line: => String): TestEvaluation = {
		// Handles .property -calls separately
		val partsIter = element.splitIterator(Regex.dot)
		def nextPart() = {
			if (partsIter.hasNext)
				partsIter.next()
			else
				throw new SyntaxException(line, s"Expected element on line `$line`")
		}
		val primary: TestEvaluation = nextPart() match {
			// Case: last => Reads a value from the last received response
			case "last" => LastResponseValue(nextPart())
			// Case: null => Hardcoded null value
			case "null" => HardCodedValue.empty
			// Case: true => Hardcoded true value
			case "true" => HardCodedValue.trueValue
			// Case: false => Hardcoded false value
			case "false" => HardCodedValue.falseValue
			// Case: Another element => Treats it as a variable reference
			case part => ReadValue(part)
		}
		// If property calls were involved, resolves them
		partsIter.foldLeft(primary) { _ get _ }
	}
	
	/**
	 * Splits the input into:
	 *      1. Quotations
	 *      1. Arrays
	 *      1. Code tokens separated by whitespace
	 * @param code Code to split
	 * @return An iterator that yields the split code parts and their roles
	 */
	private def tokensIteratorFrom(code: String): Iterator[(String, CodeRole)] =
		partsIteratorFrom(code).flatMap { case part @ (content, role) =>
			if (role == Code)
				content.splitIterator(Regex.whitespace).filter { _.nonEmpty }.map { _ -> role }
			else
				Single(part)
		}
	/**
	 * Splits the input into:
	 *      1. Quotations
	 *      1. Arrays
	 *      1. Code sequences
	 * @param code Code to split
	 * @return An iterator that yields the split parts and their roles
	 */
	private def partsIteratorFrom(code: String): Iterator[(String, CodeRole)] =
		quotationR.divide(code).iterator.flatMap {
			case Left(normalPart) =>
				arrayR.divide(normalPart).iterator.map {
					case Left(normalPart) => normalPart -> Code
					case Right(arrayPart) => arrayPart.slice(1, arrayPart.length - 1) -> CodeArray
				}
			case Right(quotation) => Single(quotation.slice(1, quotation.length - 1) -> Quotation)
		}
	
	/**
	 * Converts the remaining code into a string.
	 * Expects only code tokens to be left.
	 * @param tokensIter An iterator that yields the remaining tokens
	 * @param line Line from which these tokens were read
	 * @throws SyntaxException If no more tokens remained or if non-code tokens were encountered
	 * @return A string from the remaining tokens
	 */
	@throws[SyntaxException]("If no more tokens were found")
	private def tokensAsCode(tokensIter: Iterator[(String, CodeRole)], line: => String) = {
		if (tokensIter.hasNext) {
			tokensIter
				.map { case (str, role) =>
					if (role == Code) str else throw new SyntaxException(line, s"Unexpected input type $role")
				}
				.mkString(" ")
		}
		else
			throw new SyntaxException(line, "Unexpected end of line")
	}
	
	@throws[SyntaxException]("If out of tokens or wrong token type encountered")
	private def expectNextTokenOfType(tokensIter: Iterator[(String, CodeRole)], expectedType: CodeRole,
	                                  line: => String) =
	{
		val (part, role) = expectNextToken(tokensIter, line)
		if (role == expectedType)
			part
		else
			throw new SyntaxException(line, s"Expected $expectedType, got $role")
	}
	@throws[SyntaxException]("If out of tokens")
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
