package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.generic.casting.ValueConversions._
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
	override def toString = s"Read CSV from `$getPath`"
	
	override def run()(implicit context: TestContext): Value = {
		getPath.run().tryString match {
			case Success(path) =>
				val parseResult = {
					if (path.endsWithIgnoreCase(".csv")) {
						context.log(s"Parsing CSV file: $path")
						CsvRows.from.path(path)
					}
					else {
						context.log(s"Parsing CSV directly from `$getPath`")
						CsvRows.from.string(path)
					}
				}
				parseResult match {
					case Success(rows) => rows
					case Failure(error) =>
						val message = s"Failure while parsing CSV from `$getPath`"
						context.scribe(error, message)
						throw new TestFailedException(message, error)
				}
			
			case Failure(error) =>
				context.scribe.in("read.CSV.resolvePath")(error, "Couldn't resolve the CSV file path to read",
					Model.from("getPath" -> getPath.toString))
				throw new TestFailedException(s"Couldn't resolve CSV path using `$getPath`", error)
		}
	}
}
