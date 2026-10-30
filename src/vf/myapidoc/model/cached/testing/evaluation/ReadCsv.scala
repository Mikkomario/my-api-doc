package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.parse.file.CsvRows
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException
import vf.myapidoc.util.Common._

import scala.util.{Failure, Success}

/**
 * Reads a CSV file and yields it as a value
 * @param getPath Used for getting the CSV path or raw CSV data
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ReadCsv(getPath: TestEvaluation) extends TestEvaluation
{
	// IMPLEMENTED  ----------------------
	
	override def toString = s"Read CSV from `$getPath`"
	
	override def sensitive(implicit context: TestContext): Boolean = false
	
	override def run()(implicit context: TestContext): Value =
		getPath.run().tryString match {
			case Success(path) =>
				val parseResult = {
					// Case: A path acquired => Parses the file contents
					if (path.endsWithIgnoreCase(".csv")) {
						context.log(s"Parsing CSV file: $path")
						CsvRows.from.path(path)
					}
					// Case: Another type of value acquired => Attempts to parse the value as CSV
					else {
						context.log(s"Parsing CSV directly from `$getPath`")
						CsvRows.from.string(path)
					}
				}
				parseResult match {
					// Case: Parsing succeeded => Returns the parsed rows
					case Success(rows) => rows
					// Case: Parsing fails => Fails
					case Failure(error) =>
						val message = s"Failure while parsing CSV from `$getPath`"
						context.scribe(error, message)
						throw new TestFailedException(message, error)
				}
			
			// Case: No valid path or value acquired => Fails
			case Failure(error) =>
				context.scribe.in("read.CSV.resolvePath")(error, "Couldn't resolve the CSV file path to read",
					Model.from("getPath" -> getPath.toString))
				throw new TestFailedException(s"Couldn't resolve CSV path using `$getPath`", error)
		}
}
