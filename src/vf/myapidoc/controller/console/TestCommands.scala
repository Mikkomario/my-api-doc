package vf.myapidoc.controller.console

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Pair, Single}
import utopia.flow.collection.mutable.iterator.OptionsIterator
import utopia.flow.error.ErrorExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import utopia.flow.operator.Identity
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.StringUtils
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.result.TryExtensions._
import utopia.flow.view.mutable.Pointer
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import utopia.vault.database.Connection
import utopia.vault.store.StoreResult
import vf.myapidoc.controller.testing.{TestContext, TestParser}
import vf.myapidoc.database.access.test.file.AccessTestFiles
import vf.myapidoc.database.access.test.url.AccessTestUrls
import vf.myapidoc.database.store.TestsDb
import vf.myapidoc.model.cached.testing.Test
import vf.myapidoc.model.combined.api.DetailedApiNode
import vf.myapidoc.model.error.{SyntaxException, TestFailedException}
import vf.myapidoc.model.stored.api.Api
import vf.myapidoc.util.Common._

import java.nio.file.Path
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

/**
 * Provides console commands for executing tests
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
object TestCommands
{
	// ATTRIBUTES   -------------------------
	
	private implicit val scribe: Scribe = newScribe("console.Commands.Test")
	
	private val testsP = Pointer.eventful.empty[Tests]
	
	private val testC = Command("test", help = "Enters the test console or runs a single test")(
		ArgumentSchema("api-name", "api", help = "Name of the API to test", defaultDescription = "Selected"),
		ArgumentSchema("api-path", "url",
			help = "URL to the tested API root node, including the protocol. \nAlternatively, may refer to a previously stored URL.",
			defaultDescription = "Selected or requested"),
		ArgumentSchema("tests-path", "path", help = "Path to the file or directory that contains the test code",
			defaultDescription = "Selected"),
		ArgumentSchema("test", help = "Name of the test to run", defaultDescription = "Opens the console instead")) {
		args =>
			// Collects the required data:
			//      1. Targeted API
			//      2. API URL
			//      3. Path to the tests file
			cPool
				.logging { implicit c =>
					resolveApi(args("api-name")).flatMap { api =>
						resolveRootUrl(api, args("api-path")).flatMap { url =>
							resolveTestsPath(api.id, args("tests-path")).map { (api, url, _) }
						}
					}
				}
				.flatten
				.foreach { case (api, url, path) =>
					implicit val nodes: Seq[DetailedApiNode] = api.nodesIterator.toVector
					val parsedTests = parseTests(path.path)
					if (parsedTests.nonEmpty) {
						// Activates the test console commands
						val tests = new Tests(api.id, api.name, url.rootUrl, parsedTests, path.path)
						testsP.setOne(tests)
						args("test").string.foreach(tests.run)
					}
				}
	}
	
	// The following commands assume that testsP has been defined
	
	private val listTestsC = Command.withoutArguments("list", "ls", help = "Lists all tests") {
		testsP.value.foreach { _.list() }
	}
	private val showTestC = Command("show", help = "Shows a tests's code")(
		ArgumentSchema("test-name", "test", help = "Name of the test to show", defaultDescription = "Select")) {
		args =>
			testsP.value.foreach { tests =>
				val test = args("test-name").string match {
					case Some(testName) => tests.resolveTest(testName)
					case None =>
						println("\nWhich test do you want to show?")
						StdIn.selectFrom(tests.all.view.map { t => t -> t.name }.toVector, "Tests")
				}
				test.foreach { test =>
					println(s"\n${ test.name }")
					println("-" * test.name.length)
					test.lines.foreach(println)
				}
			}
	}
	private val runC = Command("run", help = "Runs a test")(
		ArgumentSchema("test-name", "test", help = "Name of the test to run", defaultDescription = "Select")) { args =>
		testsP.value.foreach { tests =>
			args("test-name").string match {
				case Some(testName) => tests.run(testName)
				case None =>
					println("\nWhich test do you want to run?")
					StdIn.selectFrom(tests.all.view.map { t => t -> t.name }.toVector, "Tests").foreach { test =>
						tests.run(test)
					}
			}
		}
	}
	private val reloadC = Command.withoutArguments("reload",
		help = "Reloads the tests by reading the tests file again") { testsP.value.foreach { _.reload() } }
	
	private val evalC = Command("context:evaluate", "eval",
		help = "Evaluates code in the current test context")(
		ArgumentSchema("code", help = "Code to evaluate", defaultDescription = "Opens code terminal mode")) {
		args =>
			testsP.value.foreach { tests =>
				args("code").string match {
					// Case: Evaluating a specific expression
					case Some(code) =>
						Try
							.apply {
								TestParser.parseCode("Command", Single(code))(tests.nodes) match {
									case Some(test) => tests.run(test)
									case None => println("There was no code to run")
								}
							}
							.failure
							.foreach { error =>
								println(s"Failed to compile the code: ${ error.getMessage }")
								error.causesIterator.foreach { e => println(e.getMessage) }
							}
					// Case: No code parameter => Starts code terminal mode
					case None => tests.evalMode()
				}
			}
	}
	
	private val printContextC = Command("context:print", "show", help = "Prints the current test context")(
		ArgumentSchema.flag("showSensitive", "S", help = "Whether to show sensitive values")) {
		args =>
			testsP.value.foreach { tests =>
				val showSensitive = args("showSensitive").getBoolean
				val context = tests.context
				println(StringUtils.asciiTableFrom[Constant](context.currentState.nonEmptyProperties.sortBy { _.name },
					Pair(
						"Variable" -> { _.name },
						"Value" -> { prop =>
							if (showSensitive || prop.value.isEmpty || !context.isSensitive(prop.name))
								prop.value.toJson.splitToLinesIterator(60).mkString("\n")
							else
								"*" * prop.value.getString.length
						}
					),
					"Context", skipLineSeparators = true)
				)
			}
	}
	private val listPersistedC = Command("context:persisted", help = "Lists the currently persisted property names")(
		ArgumentSchema.flag("values", "V", help = "Whether values should also be displayed")) { args =>
		testsP.value.foreach { tests =>
			val keys = tests.context.persistedKeys
			if (keys.isEmpty)
				println("No variables are persisted at this time")
			else {
				if (args("values").getBoolean) {
					println(StringUtils.asciiTableFrom[String](keys,
						Pair(
							"Variable" -> Identity,
							"Value" -> { k => tests.context(k).toJson }
						),
						"Persisted variables", skipLineSeparators = true))
				}
				else {
					println(s"Persisted variables:")
					keys.foreach { key => println(s"- $key") }
				}
			}
		}
	}
	private val resetC = Command.withoutArguments("context:reset",
		help = "Resets the context to a state matching program startup") {
		testsP.value.foreach { _.resetContext() }
	}
	private val clearC = Command.withoutArguments("context:clear",
		help = "Clears the current test context, including persisted values") {
		testsP.value.foreach { _.clearContext() } }
	
	/**
	 * A pointer that contains the currently available test commands
	 */
	val pointer = testsP.map[Seq[Command]] { tests =>
		if (tests.isDefined)
			Vector(listTestsC, showTestC, runC, evalC, printContextC, listPersistedC, reloadC, resetC, clearC, testC)
		else
			Single(testC)
	}
	
	
	// OTHER    ---------------------------
	
	//noinspection ConvertibleToMethodValue
	private def resolveRootUrl(api: Api, pathArg: Value)(implicit connection: Connection) =
		pathArg.string match {
			case Some(input) =>
				if (input.startsWithIgnoreCase("http"))
					Some(TestsDb.storeUrl(api.id, input, name = StdIn.readLine("Name of this API URL (optional): ")))
				else
					 AccessTestUrls.ofApi(api.id).containing(input).pull.bestMatch { _.name ~== input }.emptyOneOrMany
						 .flatMap {
							 case Left(only) => Some(only)
							 case Right(options) =>
								 println("\nWhich URL did you mean?")
								 StdIn.selectFrom(options.map { o => o -> o.rootUrl }, "URLs")
						 }
						 .map { StoreResult.existed(_) }
			case None =>
				println("\nSelect targeted API URL")
				StdIn.selectFromOrAdd(
					options = AccessTestUrls.ofApi(api.id).pull
						.map { u => StoreResult.existed(u) -> s"${ u.name }: ${ u.rootUrl }" }, target = "URLs") {
					StdIn.readNonEmptyLine("URL of the API root node (including protocol):").flatMap { input =>
						if (input.startsWithIgnoreCase("http"))
							Some(TestsDb.storeUrl(api.id, input,
								name = StdIn.readLine("Name of this API URL (optional): ")))
						else {
							println(s"`$input` doesn't contain the HTTP protocol (\"http\" or \"https\")")
							None
						}
					}
				}
		}
		
	private def resolveTestsPath(apiId: Int, pathArg: Value)(implicit connection: Connection) =
		pathArg.string match {
			case Some(input) => storeTestsPath(apiId, input: Path)
			case None =>
				println("\nSelect targeted tests")
				StdIn.selectFromOrAdd(
					options = AccessTestFiles.forApi(apiId).pull.map { f => StoreResult.existed(f) -> f.path.toJson },
					target = "tests") {
					StdIn.readNonEmptyLine("Path to the tests file or directory:").flatMap { storeTestsPath(apiId, _) }
				}
		}
	private def storeTestsPath(apiId: Int, path: Path)(implicit connection: Connection) = {
		if (path.exists)
			Some(TestsDb.storeTestPath(apiId, path))
		// Case: Non-existing path => Fails
		else {
			println(s"${ path.toAbsolutePath } doesn't exist")
			None
		}
	}
	
	private def parseTests(path: Path)(implicit nodes: Iterable[DetailedApiNode]): Seq[Test] = {
		// Case: Directory passed => Parses all test files in that directory and its subdirectories
		if (path.isDirectory)
			path
				.iterateChildren { pathsIter =>
					pathsIter
						.flatMap { path =>
							if ((path.fileType ~== "md") || path.isDirectory)
								parseTests(path)
							else
								Empty
						}
						.toOptimizedSeq
				}
				.logWith("Failed to iterate over potential test files", Model.from("directory" -> path.toJson),
					subContext = "parse.tests.directory.scanFiles")
				.getOrElse(Empty)
		// Case: Test file passed => Attempts to parse it
		else
            TestParser(path) match {
                case Success(tests) =>
                    if (tests.isEmpty)
                        scribe.in("parse.tests.noTests").warning("No tests were found",
	                        Model.from("path" -> path.toJson))
	                tests
                case Failure(error) =>
                    scribe.in("parse.tests")(error, "Failed to parse the tests", Model.from("path" -> path.toJson))
                    error match {
                        case error: SyntaxException =>
                            println(s"\nParsing of ${ path.fileName } failed at line: `${
                                error.line }`\nCause: ${ error.getMessage }${
	                            error.causesIterator.map { e => s"\n${ e.getMessage }" }.mkString }\nPlease edit the file and make the necessary corrections.")
                            if (StdIn.ask("\nDo you want to try again now?"))
                                parseTests(path)
                            else
                                Empty
                        
                        case _ => Empty
                    }
            }
	}
	
	
	// NESTED   ------------------------
	
	private class Tests(apiId: Int, apiName: String, apiRootUrl: String, initialTests: Iterable[Test], path: Path)
	                   (implicit val nodes: Iterable[DetailedApiNode])
	{
		// ATTRIBUTES   -----------------
		
		private val testsP = Pointer.eventful(initialTests)
		private val mappedTestsP =
			testsP.map { _.iterator.map { t => standardize(t.name) -> t }.toMap }
		implicit val context: TestContext = new TestContext(apiId, apiName, apiRootUrl, testsP,
			sensitiveApiPropertyNames = nodes.iterator.flatMap { _.sensitivePropertyNamesIterator }.toSet)
		
		
		// COMPUTED ---------------------
		
		def all = testsP.value
		
		
		// OTHER    ---------------------
		
		def run(testName: String): Unit = resolveTest(testName).foreach(run)
		def run(test: Test): Unit = {
			println(s"Running \"${ test.name }\"...")
			Try { test.run() } match {
				case Success(_) => println("Test passed successfully")
				case Failure(error) =>
					scribe.in("run").variant("test", test.name)(error, "Test failed")
					println(s"\nTest failed with: ${ error.getMessage }")
					error.causesIterator.foreach { e => println(s"Cause: ${ e.getMessage }") }
					
					// Writes the failed code
					error match {
						case e: TestFailedException => writeFailedCode(e)
						case _ => ()
					}
					
					chooseNextAction(test)
			}
		}
		
		def resolveTest(testName: String) = {
			val key = standardize(testName)
			val tests = mappedTestsP.value
			tests.get(key).orElse {
				tests.iterator.filter { _._1.contains(key) }.map { _._2 }.toOptimizedSeq.emptyOneOrMany match {
					case None =>
						println(s"No test matches \"$testName\"")
						None
					case Some(Left(test)) => Some(test)
					case Some(Right(options)) =>
						println("\nWhich test did you mean?")
						StdIn.selectFrom(options.map { t => t -> t.name }, "tests")
				}
			}
		}
		
		def evalMode() = {
			context.log("\nEval mode started\n")
			
			println("\nWrite test code to execute. An empty line terminates this mode.")
			val source = OptionsIterator.continually {
				StdIn.readNonEmptyLine("Next instruction:").filterNot { _ ~== "exit" }
			}
			while (source.hasNext) {
				Try { TestParser.parseAndExecute(source) }.failure.foreach { error =>
					println(s"Failure: ${ error.getMessage }")
					error.causesIterator.foreach { e => println(e.getMessage) }
				}
			}
			
			context.log("\nEval mode ended\n")
		}
		
		def list() = println(StringUtils.asciiTableFrom[Test](testsP.value.toOptimizedSeq,
			Pair(
				"Test" -> { _.name },
				"Description" -> { _.description.splitToLinesIterator(60).mkString("\n") }
			),
			"Available tests"))
		
		def reload() = {
			val tests = parseTests(path)
			if (tests.isEmpty) {
				println("Failed to reload tests")
				false
			}
			else {
				testsP.value = tests
				println("Tests reloaded")
				true
			}
		}
		def resetContext() = {
			context.reset()
			println("Context reset")
		}
		def clearContext() = {
			context.clear()
			println("Context cleared")
		}
		
		private def writeFailedCode(error: TestFailedException): Unit = {
			if (error.line >= 0)
				error.test.ifNotEmpty.flatMap { t => mappedTestsP.value.get(standardize(t)) }.foreach { test =>
					println(s"\n${ test.name }\n${ "-" * test.name.length }")
					test.lines.take(error.line + 1).foreach(println)
					test.lines.lift(error.line).foreach { lastLine =>
						println("^" * lastLine.length)
						println(error.message)
					}
				}
			
			Option(error.getCause)
				.map {
					case e: TestFailedException => Right(e)
					case e => Left(e)
				}
				.foreach {
					case Right(e) => writeFailedCode(e)
					case Left(e) =>
						println(e.getMessage)
						e.causesIterator.foreach { e => println(e.getMessage) }
				}
		}
		
		private def chooseNextAction(test: Test): Unit = {
			println("\nWhat do you want to do?")
			StdIn.selectFrom(Vector(
				1 -> "Try again", 2 -> "Reload tests", 3 -> "Open code terminal", 4 -> "Open event log",
					4 -> "End test (default)"))
				.foreach {
					case 1 => run(test)
					case 2 =>
						if (reload())
							run(test.name)
					case 3 =>
						evalMode()
						chooseNextAction(test)
					case 4 =>
						context.openEventLog()
						chooseNextAction(test)
						
					case _ => ()
				}
		}
		
		private def standardize(str: String) = str.toLowerCase.filter { _.isLetterOrDigit }.mkString
	}
}
