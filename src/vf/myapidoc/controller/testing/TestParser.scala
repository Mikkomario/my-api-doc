package vf.myapidoc.controller.testing

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.model.enumeration.ValueOperation2
import utopia.flow.generic.model.immutable.Value
import utopia.flow.operator.sign.Sign
import utopia.flow.operator.sign.Sign.{Negative, Positive}
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import utopia.flow.view.mutable.Pointer
import vf.myapidoc.controller.MdParser
import vf.myapidoc.model.cached.testing.Test
import vf.myapidoc.model.cached.testing.evaluation.ComparisonOperator.{Equality, Inequality}
import vf.myapidoc.model.cached.testing.evaluation._
import vf.myapidoc.model.cached.testing.instruction._
import vf.myapidoc.model.combined.api.DetailedApiNode
import vf.myapidoc.model.error.SyntaxException

import java.nio.file.Path
import scala.annotation.tailrec
import scala.collection.mutable

/**
 * An interface for parsing tests from Markdown files
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
object TestParser
{
	// COMPUTED ------------------------
	
	// Expects the line to end afterwards
	private def nextAuthInstruction(implicit tokensIter: TokensIterator): TestInstruction = {
		val firstPart = nextEvalElement(None)
		tokensIter.nextOption() match {
			// Case: No + token found => Applies bearer token authorization
			case None => UpdateAuth(lineRemainderAsEval)
				// Case: + found, signifying that basic authorization is used => Parses the password part, also
			case Some(SignOperator(Positive)) => UpdateBasicAuth(Pair(firstPart, lineRemainderAsEval))
				// Case: Unexpected continuation => Fails compilation
			case other =>
				throw new SyntaxException(tokensIter.line, s"Unexpected authorization follow-up token: $other")
		}
	}
	
	private def lineRemainderAsEval(implicit tokensIter: TokensIterator): TestEvaluation =
		nextEval(None, expectLineEnd = true)
	
	@throws[SyntaxException]("If out of tokens")
	private def nextToken(implicit tokensIter: TokensIterator) = {
		if (tokensIter.hasNext)
			tokensIter.next()
		else
			throw new SyntaxException(tokensIter.line, "Unexpected end of line")
	}
	
	
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
									.filterNot { _.forall { c => c == ' ' || c == '\t' } },
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
	 *                      - Lines containing only tabs or whitespaces are considered empty
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
			implicit val tokensIter: TokensIterator = TokensIterator.fromLine(line)
			// Expects the line to start with a code token (a keyword or a variable reference)
			val firstToken = nextToken match {
				case Code(code) => code
				case other => throw new SyntaxException(line, s"Unexpected start of line: `$other`")
			}
			firstToken.toLowerCase.notEndingWith(":") match {
				// Case: If statement => Parses the condition and the conditional code block
				case "if" =>
					val condition = lineRemainderAsEval
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					terminator match {
						// Case: Code continues after the if block => Checks whether an else block follows
						case Some(nextLine) =>
							// Case: Else block follows
							//       => Parses the else code block and it to the if-sequence
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
					val condition = lineRemainderAsEval
					val (code, terminator) = parseIndentedCodeBlock(linesIter, line)
					builder += ExecuteWhile(condition, code)
					terminator.foreach(proceedAfter)
				// Case: Assertion => Parses the asserted expression
				case "assert" => builder += TestAssertion(lineRemainderAsEval)
				// Case: Persist statement => Parses the persist target properties
				case "persist" =>
					nextToken match {
						// Case: Targets specified as an array
						//       => Persists each element, but only if each consists of a single code element
						case CodeArray(elements) =>
							val persistTargets = elements.map { parts =>
								parts.only match {
									case Some(Code(target)) => target
									case _ =>
										throw new SyntaxException(line,
											s"Malformed persist target `${ parts.mkString(" ") }`")
								}
							}
							builder += Persist(persistTargets)
						// Case: Code element => Persists that element, assuming it's a variable reference
						case Code(code) => builder += Persist(Single(code))
						// Case: Other type of element => Fails compilation
						case other =>
							throw new SyntaxException(line, s"Expected code or array as a persist target, but got `$other`")
					}
				// Case: Wait statement => Parses the wait duration
				case "wait" => builder += ProceedAfterWait(lineRemainderAsEval)
				// Case: Use authorization statement => Parses an authorization change instruction
				case "use" => builder += nextAuthInstruction
				// Case: Run statement => Parses the code to run another test
				case "run" =>
					tokensIter.mkString(" ").ifNotEmpty match {
						case Some(testName) => builder += RunTest(testName)
						case None => throw new SyntaxException(line, "Expected a test name after `run`")
					}
				// Case: FAIL statement => Parses the code to fail the test
				case "fail" =>
					// The remaining part, if present, is included as a failure message
					builder += {
						if (tokensIter.hasNext)
							FailTest(getMessage = lineRemainderAsEval)
						else
							FailTest()
					}
				// Case: Print => Parses the code to print a value to the console
				case "print" => builder += PrintValue(lineRemainderAsEval)
				case other =>
					// Checks whether the line starts with a method name
					Method.values.find { _.name.toLowerCase == other } match {
						// Case: Method prefix => Parses code for executing an HTTP request
						case Some(method) =>
							// Parses the request target
							val target = tokensIter.mkString(" ")
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
							nextToken match {
								// Case: Assignment found => Parses the assigned value and adds an assignment
								case Assignment =>
									val value = lineRemainderAsEval
									builder += (firstToken.split(Regex.dot).oneOrMany match {
										case Left(key) => Assign(key, value)
										case Right(path) => AssignPropertyValue(path, value)
									})
								// Case: No assignment found (either) => Fails compilation
								case other =>
									throw new SyntaxException(line, s"Expected `=` after `$firstToken`, got `$other`")
							}
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
	
	/**
	 * Parses the line remainder into an evaluation / expression
	 * @param parameterReference Reference to an external parameter, if applicable. Call-by-name.
	 * @param expectLineEnd Whether to expect the line to end afterwards. Default = false.
	 * @param tokensIter An iterator that yields the remaining tokens / elements on this line
	 * @return Parsed evaluation
	 */
	@throws[SyntaxException]("If parsing failed")
	private def nextEval(parameterReference: => Option[TestEvaluation], expectLineEnd: Boolean = false)
	                    (implicit tokensIter: TokensIterator): TestEvaluation =
	{
		// Parses the first token / element
		val primary = nextEvalElement(parameterReference)
		val result = {
			// Case: Not the end of the line => Expects a binary operator
			if (tokensIter.hasNext) {
				lazy val remainder = nextEval(parameterReference, expectLineEnd)
				nextToken match {
					case Comparison(operator) => Compare(Pair(primary, remainder), operator)
					case SignOperator(sign) => Combine(Pair(primary, remainder), ValueOperation2(sign))
					case other =>
						throw new SyntaxException(tokensIter.line, s"Expected a binary operator, got `$other`")
				}
			}
			else
				primary
		}
		// Makes sure the line's been exhausted, if required
		if (expectLineEnd && tokensIter.hasNext)
			throw new SyntaxException(tokensIter.line, s"Expected end of line, got ${ tokensIter.next() }")
		// Case: End of line reached => Returns the parsed evaluation
		else
			result
	}
	
	/**
	 * Extracts the next evaluation / expression element that appears on the line.
	 * Does not expect a binary operator here.
	 * @param parameterReference A parameter reference. Call-by-name.
	 * @param tokensIter An iterator that yields additional tokens.
	 * @return Parsed evaluation (element).
	 */
	@throws[SyntaxException]("If parsing failed")
	private def nextEvalElement(parameterReference: => Option[TestEvaluation])
	                           (implicit tokensIter: TokensIterator): TestEvaluation =
		nextToken match {
			// Case: Quotation => Yields a hard-coded string value
			case Quotation(text) => HardCodedValue.string(text)
			// Case: Array => Yields an array of evaluations / expressions
			case CodeArray(elements) =>
				EvaluateArray(elements.map { evalFrom(_, parameterReference, tokensIter.line) })
			// Case: + or - => Potentially inverts the next element (assumes that this is a unary operator)
			case SignOperator(sign) =>
				val next = nextEvalElement(parameterReference)
				sign match {
					case Positive => next
					case Negative => ReverseValue(next)
				}
			// Case: Function call => Parses it
			case FunctionCall(function, params) => parseFunction(function, params, parameterReference)
			// Case: Code token => Checks for custom commands
			case Code(code) =>
				code.toLowerCase match {
					// Case: Prompt command => Requests a value from the user when executed
					case "prompt:" => parsePrompt(parameterReference)
					// Case: CSV command => Reads CSV based on the following expression
					case "csv:" => ReadCsv(nextEval(parameterReference))
					// Case: No custom command => Parses the element as a normal (code) evaluation
					case _ => parseCodeReference(code, parameterReference, tokensIter.line)
				}
			case other => throw new SyntaxException(tokensIter.line, s"Invalid element: `$other`")
		}
	
	private def parseFunction(function: String, params: Seq[Seq[CodeElement]],
	                          parameterReference: => Option[TestEvaluation])
	                         (implicit tokensIter: TokensIterator): TestEvaluation =
	{
		// Parses the parameters
		lazy val paramsIter = params.iterator.map { evalFrom(_, parameterReference, tokensIter.line) }
		def iterateCollection(createFunction: (TestEvaluation, TestEvaluation, Pointer[Value]) => TestEvaluation) = {
			paramsIter.nextOption() match {
				case Some(coll) =>
					val itemP = Pointer(Value.empty)
					val collectionItemReference = ReadPointer(itemP)
					val predicate = nextEval(Some(collectionItemReference))
					
					createFunction(coll, predicate, itemP)
				
				case None =>
					throw new SyntaxException(tokensIter.line, s"Expected a collection parameter in `$function`")
			}
		}
		
		function match {
			// Case: Find function => Iterates over the parameter to find a value
			case "find" => iterateCollection { (coll, predicate, itemP) => FindFunction(coll, predicate, itemP) }
			// Case: Exists function => Iterates over the parameter to see whether any value satisfies a predicate
			case "exists" => iterateCollection { (coll, predicate, itemP) => ExistsFunction(coll, predicate, itemP) }
			// Case: Contains function
			//       => Iterates over the parameter to see whether any value is equal to the searched element
			case "contains" =>
				paramsIter.nextOption() match {
					case Some(coll) =>
						// The searched element is either the second parameter, or the following evaluation
						val searched = paramsIter.nextOption().getOrElse { nextEval(parameterReference) }
						// Utilizes an ExistsFunction
						val itemP = Pointer(Value.empty)
						val collectionItemReference = ReadPointer(itemP)
						val predicate = Compare(Pair(collectionItemReference, searched), Equality)
						ExistsFunction(coll, predicate, itemP)
					
					case None =>
						throw new SyntaxException(tokensIter.line, s"Expected collection parameter in `$function`")
				}
				
			// Case: No function call either => Parses the element as a normal evaluation
			case other => throw new SyntaxException(tokensIter.line, s"Unknown function `$other`")
		}
	}
	
	private def evalFrom(elements: Seq[CodeElement], parameterReference: => Option[TestEvaluation],
	                     line: => String): TestEvaluation =
		nextEval(parameterReference, expectLineEnd = true)(TokensIterator.from(elements, line))
	
	@tailrec
	private def parsePrompt(parameterReference: => Option[TestEvaluation], sensitive: Boolean = false)
	                       (implicit tokensIter: TokensIterator): PromptForValue =
	{
		// Next expects either "sensitive:" or a prompt expression
		nextToken match {
			case Code(code) =>
				// Case: Sensitive parameter found => Expects the prompt to follow
				if (!sensitive && code.startsWithIgnoreCase("sensitive:"))
					parsePrompt(parameterReference, sensitive = true)
				else
					PromptForValue(nextEval(parameterReference), sensitive)
				
			// Case: Prompt as a quotation
			case Quotation(prompt) => PromptForValue(HardCodedValue.string(prompt), sensitive)
			// Case: Function as a prompt => Parses the function and passes it to the prompt function
			case FunctionCall(function, params) =>
				PromptForValue(parseFunction(function, params, parameterReference), sensitive)
			case other =>
				throw new SyntaxException(tokensIter.line, s"Unexpected prompt parameter: `$other`")
		}
	}
	
	/**
	 * Converts a single code token into an evaluation / expression
	 * @param code Code element to parse. Should represent a simple value or a chain of property calls.
	 * @param parameterReference Parameter reference, if applicable. Call-by-name.
	 * @param line Line on which this element appears. Used in error logging.
	 * @return Parsed evaluation / expression
	 */
	@throws[SyntaxException]("If parsing failed")
	private def parseCodeReference(code: String, parameterReference: => Option[TestEvaluation],
	                               line: => String): TestEvaluation =
	{
		// Handles .property -calls separately
		val partsIter = code.splitIterator(Regex.dot)
		def nextPart() = {
			if (partsIter.hasNext)
				partsIter.next()
			else
				throw new SyntaxException(line, "Expected an element")
		}
		val primary: TestEvaluation = nextPart() match {
			// Case: last => Reads a value from the last received response
			case "last" =>
				if (partsIter.hasNext)
					LastResponseValue(partsIter.next())
				else
					LastResponseBody
			// Case: null => Hardcoded null value
			case "null" => HardCodedValue.empty
			// Case: true => Hardcoded true value
			case "true" => HardCodedValue.trueValue
			// Case: false => Hardcoded false value
			case "false" => HardCodedValue.falseValue
			// Case: now => Current timestamp (when first called)
			case "now" => CurrentTimestamp
			// Case: _ => Reference to a parameter
			case "_" =>
				parameterReference.getOrElse {
					throw new SyntaxException(line, "Encountered a reference to a non-existing parameter")
				}
			case part =>
				// Case: String literal (although not likely to appear here) => Treats it as a hard-coded value
				if (part.startsWith("\"") && part.endsWith("\""))
					HardCodedValue.string(part.substring(1, part.length - 1))
				else {
					import utopia.flow.generic.casting.ValueConversions._
					part.toIntOption match {
						// Case: Number literal => Treats it as a hard-coded value
						case Some(number) => HardCodedValue(number)
						case None =>
							part.toDoubleOption match {
								// Case: Double number literal => Treats it as a hard-coded value
								case Some(number) => HardCodedValue(number)
								// Case: Another element => Treats it as a variable reference
								case None => ReadValue(part)
							}
					}
				}
		}
		// If property calls were involved, resolves them
		partsIter.foldLeft(primary) { _ get _ }
	}
	
	
	// NESTED   -----------------------
	
	/**
	 * An enumeration for different code tokens
	 */
	private sealed trait CodeElement
	
	/**
	 * Represents a string literal
	 * @param text Wrapped string
	 */
	private case class Quotation(text: String) extends CodeElement
	{
		override def toString: String = s"\"$text\""
	}
	
	/**
	 * Represents an array of other elements
	 * @param elements The elements in this array. Each entry may consist of multiple tokens
	 */
	private case class CodeArray(elements: Seq[Seq[CodeElement]]) extends CodeElement
	{
		override def toString: String = s"[${ elements.iterator.map { _.mkString }.mkString(", ") }]"
	}
	
	/**
	 * Represents an assignment operator (=)
	 */
	private case object Assignment extends CodeElement
	{
		override def toString: String = "="
	}
	/**
	 * Represents an unary or binary + or - operator
	 * @param sign Sign of this operator
	 */
	private case class SignOperator(sign: Sign) extends CodeElement
	{
		override def toString: String = sign.toString
	}
	/**
	 * Represents a comparison operator
	 * @param operator The applied operator
	 */
	private case class Comparison(operator: ComparisonOperator) extends CodeElement
	{
		override def toString: String = operator.toString
	}
	
	/**
	 * Represents a function call
	 * @param function Name of the called function
	 * @param parameters Passed parameters
	 */
	private case class FunctionCall(function: String, parameters: Seq[Seq[CodeElement]]) extends CodeElement
	{
		override def toString: String = s"$function(${ parameters.iterator.map { _.mkString }.mkString(", ") })"
	}
	
	/**
	 * Represents code that doesn't fall to any other category
	 * @param text The code text
	 */
	private case class Code(text: String) extends CodeElement
	{
		override def toString: String = text
	}
	
	/**
	 * Iterates over list elements, such as array elements or function parameters.
	 * Splits by comma (,).
	 * @param source An iterator that yields the characters to iterate over.
	 *               Expects trimmed content that only and fully spans the list area.
	 * @param line A line from which the parsed list is found. Used in debug logging.
	 */
	private class ListElementsIterator(source: Iterator[Char], line: => String) extends Iterator[Seq[CodeElement]]
	{
		// ATTRIBUTES   --------------------
		
		private val cache = Pointer.empty[Char]
		
		
		// IMPLEMENTED  --------------------
		
		override def hasNext: Boolean = cache.nonEmpty || source.hasNext
		
		override def next(): Seq[CodeElement] = {
			// Collects the next list item
			val result = TokensIterator(cache.pop().iterator ++ source.takeWhile { _ != ',' }, line).toOptimizedSeq
			// Trims the next list element
			cache.value = source.find { _ != ' ' }
			result
		}
	}
	
	private object TokensIterator
	{
		// ATTRIBUTES   --------------------
		
		/**
		 * Characters that terminate [[Code]] and function name elements (exclusive)
		 */
		private val stopChars = Set(' ', '[', '"', '<', '>', '=', '-', '+', '(')
		
		
		// OTHER   -------------------------
		
		/**
		 * @param line A complete line of code
		 * @return An iterator that yields all tokens / elements that appear on that line
		 */
		def fromLine(line: String): TokensIterator = new _TokensIterator(line.trim.iterator, line)
		/**
		 * @param tokens An iterator that yields tokens / code elements
		 * @param line Targeted line (call-by-name)
		 * @return A token iterator that wraps the specified iterator
		 */
		def from(tokens: IterableOnce[CodeElement], line: => String): TokensIterator =
			new TokensIteratorWrapper(tokens.iterator, line)
		
		/**
		 * @param source An iterator that yields the characters to iterate over. Expected to be trimmed.
		 * @param line The read code line (call-by-name)
		 * @return A new tokens iterator
		 */
		def apply(source: Iterator[Char], line: => String): TokensIterator = new _TokensIterator(source, line)
		
		private class TokensIteratorWrapper(wrapped: Iterator[CodeElement], _line: => String) extends TokensIterator
		{
			override def line: String = _line
			override def hasNext: Boolean = wrapped.hasNext
			
			override def next(): CodeElement = wrapped.next()
		}
		
		private class _TokensIterator(source: Iterator[Char], _line: => String) extends TokensIterator
		{
			import TokensIterator.stopChars
			
			// ATTRIBUTES   ---------------------
			
			private val cache = Pointer.empty[Char]
			
			
			// IMPLEMENTED  ---------------------
			
			override def line = _line
			override def hasNext: Boolean = cache.nonEmpty || source.hasNext
			
			// Determines the type of the next element based on the next character
			override def next(): CodeElement = cache.pop().getOrElse { source.next() } match {
				// Case: Quote start => Consumes and wraps the whole quotation
				case '"' => Quotation(text = source.takeWhile { _ != '"' }.mkString)
				// Case: Array start => Consumes and wraps the whole array
				case '[' =>
					CodeArray(new ListElementsIterator(new ParenthesesContentIterator(source, '[', ']'), _line)
						.toOptimizedSeq)
				// Case: '=' => Expects a comparison or an assignment
				case '=' =>
					source.next() match {
						// Case: Equality (==)
						case '=' => Comparison(Equality)
						// Case: Assignment (=)
						case other =>
							cache.setOne(other)
							Assignment
					}
				// Case: Comparison operator => Determines the exact operator
				case '<' => readComparisonOperator('<')
				case '>' => readComparisonOperator('>')
				// Case: '!' => Expects an inequality, but also supports as an alias for negation (-)
				case '!' =>
					source.next() match {
						case '=' => Comparison(Inequality)
						case other =>
							cache.setOne(other)
							SignOperator(Negative)
					}
				// Case: Whitespace (should not, but might occur) => Ignores leading whitespaces
				case ' ' => next()
				case other =>
					// Checks for + and -
					Sign.of(other) match {
						// Case: + or - => Applies it as an operator
						case Some(sign) => SignOperator(sign)
						// Case: Other => Interprets it as a code element or a function call
						case None =>
							// Reads input until a stop character or end-of-input is reached
							val contentBuilder = new StringBuilder()
							contentBuilder += other
							var stop: Option[Char] = None
							while (stop.isEmpty && source.hasNext) {
								val c = source.next()
								if (stopChars.contains(c))
									stop = Some(c)
								else
									contentBuilder += c
							}
							
							// Case: Stopped at a function parameter list
							//       => Extracts the list and yields a function call
							if (stop.contains('(')) {
								val result = FunctionCall(function = contentBuilder.result(),
									parameters = new ListElementsIterator(
										new ParenthesesContentIterator(source, '[', ']'), _line)
										.toOptimizedSeq)
								
								// Skips whitespaces and : after function calls
								cache.value = source.find { c => c != ' ' && c != ':' }
								
								result
							}
							// Case: Other stop element => Returns the discovered code element
							else {
								cache.value = stop.filterNot { _ == ' ' }
								Code(contentBuilder.result())
							}
					}
			}
			
			
			// OTHER    ------------------------
			
			private def readComparisonOperator(starter: Char) = source.next() match {
				case '=' =>
					ComparisonOperator(s"$starter=") match {
						case Some(operator) => Comparison(operator)
						case None => throw new UnsupportedOperationException(s"Operator `$starter=` is not supported")
					}
				case other =>
					cache.setOne(other)
					ComparisonOperator(starter.toString) match {
						case Some(operator) => Comparison(operator)
						case None => throw new UnsupportedOperationException(s"Operator `$starter` is not supported")
					}
			}
		}
	}
	private trait TokensIterator extends Iterator[CodeElement]
	{
		/**
		 * @return The code line being iterated
		 */
		def line: String
	}
	
	/**
	 * Returns characters within parentheses. Handles nested parentheses.
	 * @param source An iterator that yields the remaining characters.
	 *               Expected to initially return the first character after the opening parenthesis.
	 * @param open Open parenthesis character
	 * @param close Close parenthesis character
	 */
	private class ParenthesesContentIterator(source: Iterator[Char], open: Char, close: Char) extends Iterator[Char]
	{
		// ATTRIBUTES   --------------------
		
		private val cache = Pointer.empty[Char]
		private var depth = 1
		
		
		// IMPLEMENTED  --------------------
		
		override def hasNext: Boolean = {
			// Case: End of parentheses content found => No more content
			if (depth <= 0)
				false
			// Case: Next character already cached => Content
			else if (cache.nonEmpty)
				true
			else if (source.hasNext) {
				val c = source.next()
				// Case: Nested parentheses => Increases the current depth and moves to the next character
				if (c == open) {
					depth += 1
					hasNext
				}
				// Case: End of parentheses => Decreases the current depth and moves to the next character, if needed
				else if (c == close) {
					depth -= 1
					hasNext
				}
				// Case: Other character => Caches it and considers it content
				else {
					cache.setOne(c)
					true
				}
			}
			// Case: End of source => No more content
			else
				false
		}
		
		override def next(): Char = cache.pop().getOrElse { source.next() }
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
