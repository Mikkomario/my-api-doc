package vf.myapidoc.controller.console

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.view.immutable.caching.Lazy
import utopia.flow.view.mutable.Pointer
import utopia.scribe.core.controller.logging.Scribe
import utopia.vault.database.Connection
import utopia.vault.store.StoreResult
import vf.myapidoc.controller.testing.{TestContext, TestParser}
import vf.myapidoc.database.access.test.file.AccessTestFiles
import vf.myapidoc.database.access.test.url.AccessTestUrls
import vf.myapidoc.database.store.TestsDb
import vf.myapidoc.model.cached.testing.Test
import vf.myapidoc.model.combined.api.DetailedApiNode
import vf.myapidoc.model.error.SyntaxException
import vf.myapidoc.model.stored.api.Api
import vf.myapidoc.util.Common._

import java.nio.file.Path
import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success}

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
		ArgumentSchema("tests-path", "path", help = "Path to the file that contains the test code",
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
						val tests = new Tests(api.id, url.rootUrl, parsedTests, path.path)
						testsP.setOne(tests)
						args("test").string.foreach(tests.run)
					}
				}
	}
	
	// The following commands assume that testsP has been defined
	
	private val listC = Command.withoutArguments("list", "ls", help = "Lists all tests") {
		testsP.value.foreach { _.list() }
	}
	private val runC = Command("run", help = "Runs a tests or all tests")(
		ArgumentSchema("test-name", "test", help = "Name of the test to run. Pass \"all\" to run all tests",
			defaultDescription = "Select")) {
		args =>
			testsP.value.foreach { tests =>
				args("test-name").string match {
					case Some(testName) =>
						if (testName == "all")
							tests.runAll()
						else
							tests.run(testName)
					case None =>
						println("Which test do you want to run?")
						StdIn.selectFrom(tests.all.view.map { t => t -> t.name }.toVector, "Tests").foreach { test =>
							tests.run(test)
						}
				}
			}
	}
	private val reloadC = Command.withoutArguments("reload", help = "Reloads the tests by reading the tests file again") {
		testsP.value.foreach { _.reload() }
	}
	
	/**
	 * A pointer that contains the currently available test commands
	 */
	val pointer = testsP.map[Seq[Command]] { tests =>
		if (tests.isDefined)
			Vector(listC, runC, reloadC, testC)
		else
			Single(testC)
	}
	
	
	// OTHER    ---------------------------
	
	//noinspection ConvertibleToMethodValue
	private def resolveRootUrl(api: Api, pathArg: Value)(implicit connection: Connection) =
		pathArg.string match {
			case Some(input) =>
				if (input.startsWithIgnoreCase("http"))
					Some(TestsDb.storeUrl(api.id, input, name = StdIn.readLine("Name of this API (optional): ")))
				else
					 AccessTestUrls.ofApi(api.id).containing(input).pull.bestMatch { _.name ~== input }.emptyOneOrMany
						 .flatMap {
							 case Left(only) => Some(only)
							 case Right(options) =>
								 println("Which one did you mean?")
								 StdIn.selectFrom(options.map { o => o -> o.rootUrl }, "URLs")
						 }
						 .map { StoreResult.existed(_) }
			case None =>
				StdIn.selectFromOrAdd(
					options = AccessTestUrls.ofApi(api.id).pull
						.map { u => StoreResult.existed(u) -> s"${ u.name }: ${ u.rootUrl }" }, target = "URLs") {
					StdIn.readNonEmptyLine("URL of the API root node (including protocol):").flatMap { input =>
						if (input.startsWithIgnoreCase("http"))
							Some(TestsDb.storeUrl(api.id, input,
								name = StdIn.readLine("Name of this API (optional): ")))
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
				StdIn.selectFromOrAdd(
					options = AccessTestFiles.forApi(apiId).pull.map { f => StoreResult.existed(f) -> f.path.toJson },
					target = "tests") {
					StdIn.readNonEmptyLine("Path to the tests file:").flatMap { storeTestsPath(apiId, _) }
				}
		}
	private def storeTestsPath(apiId: Int, path: Path)(implicit connection: Connection) = {
		if (path.exists) {
			// Case: Directory => Allows the user to select from the Markdown files within it (if there are any)
			if (path.isDirectory) {
				path.iterateChildren { _.filter { p => p.fileType ~== "md" }.toOptimizedSeq } match {
					case Success(files) =>
						// Case: No files to select from => Fails
						if (files.isEmpty) {
							println(s"${ path.toAbsolutePath } doesn't contain any Markdown files")
							None
						}
						else
							StdIn.selectFrom(files.map { f => f -> f.fileName }, "files")
								.map { TestsDb.storeTestPath(apiId, _) }
								
					// Case: Directory-reading failed => Fails
					case Failure(error) =>
						scribe.in("store.testsPath.listFiles")(error, "Couldn't scan the specified directory")
						None
				}
			}
			// Case: An existing test file => Stores it
			else
				Some(TestsDb.storeTestPath(apiId, path))
		}
		// Case: Non-existing path => Fails
		else {
			println(s"${ path.toAbsolutePath } doesn't exist")
			None
		}
	}
	
	@tailrec
	private def parseTests(path: Path)(implicit nodes: Iterable[DetailedApiNode]): Seq[Test] = {
		TestParser(path) match {
			case Success(tests) =>
				if (tests.isEmpty)
					println(s"No tests found in ${ path.toAbsolutePath }")
				tests
			case Failure(error) =>
				scribe.in("parse.tests")(error, "Failed to parse the tests", Model.from("path" -> path.toJson))
				error match {
					case error: SyntaxException =>
						println(s"Parsing failed at line: ${ error.line }\nPlease edit the file and make the necessary corrections.")
						if (StdIn.ask("Do you want to try again now?"))
							parseTests(path)
						else
							Empty
					
					case _ => Empty
				}
		}
	}
	
	
	// NESTED   ------------------------
	
	private class Tests(apiId: Int, apiRootUrl: String, initialTests: Iterable[Test], path: Path)
	                   (implicit nodes: Iterable[DetailedApiNode])
	{
		// ATTRIBUTES   -----------------
		
		private var tests = initialTests
		private val contextP = Lazy.resettable { new TestContext(apiId, apiRootUrl, tests) }
		
		
		// COMPUTED ---------------------
		
		def all = tests
		
		private implicit def context: TestContext = contextP.value
		
		
		// OTHER    ---------------------
		
		def runAll() = {
			val testsCount = tests.size
			println(s"Running $testsCount tests:")
			val results = tests.iterator.zipWithIndex
				.map { case (test, i) =>
					print(s"${ i + 1 }/$testsCount: ${ test.name }: ")
					val result = test.run()
					println(if (result) "passed" else "failed")
					result
				}
				.toOptimizedSeq
			val failureCount = results.count { !_ }
			if (failureCount == 0)
				println("\nAll tests passed successfully")
			else
				println(s"\n$failureCount/$testsCount tests failed")
		}
		
		def run(testName: String): Unit = tests.find { _.name ~== testName } match {
			case Some(test) => run(test)
			case None =>
				println(s"No test named \"$testName\" exists.")
				list()
		}
		def run(test: Test): Unit = {
			println(s"Running ${ test.name }...")
			if (test.run())
				println(s"${ test.name } passed successfully")
			else {
				println(s"${ test.name } failed")
				println("What do you want to do?")
				StdIn.selectFrom(Vector(1 -> "Try again", 2 -> "Reload tests", 3 -> "Move on (default)")).foreach {
					case 1 => run(test)
					case 2 =>
						if (reload())
							run(test.name)
						
					case _ => ()
				}
			}
		}
		
		def list() = {
			println("Available tests:")
			tests.foreach { test => println(s"- ${ test.name }${ test.description.prependIfNotEmpty(": ") }") }
		}
		
		def reload() = TestParser(path) match {
			case Success(tests) =>
				this.tests = tests
				println("Tests reloaded")
				contextP.reset()
				true
			case Failure(error) =>
				scribe.in("Tests.reload").recoverable(error, "Failed to reload tests",
					Model.from("path" -> path.toJson))
				false
		}
	}
}
