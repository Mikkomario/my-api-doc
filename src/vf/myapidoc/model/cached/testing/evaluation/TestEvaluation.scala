package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext

/**
 * Common trait for evaluation functions used in tests
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
trait TestEvaluation
{
	// ABSTRACT -----------------------
	
	/**
	 * @param context Applicable context
	 * @return Whether this evaluation yields sensitive information
	 */
	def sensitive(implicit context: TestContext): Boolean
	
	/**
	 * Executes this evaluation
	 * @param context Implicit test context
	 * @return Evaluation result as a value
	 */
	def run()(implicit context: TestContext): Value
	
	
	// OTHER    -----------------------
	
	/**
	 * Executes this evaluation and casts the result to a boolean.
	 * Logs if the value is empty or can't be cast to boolean.
	 * @param line Index of the line where this evaluation appears
	 * @param loggingDetails Details to add to the logging entry (optional, call-by-name)
	 * @param context Implicit test context
	 * @return Cast value or false
	 */
	def runExpectingBoolean(line: Int, loggingDetails: (String, Value)*)(implicit context: TestContext): Boolean =
		runExpecting(line, false, loggingDetails: _*) { _.boolean }
	/**
	 * Executes this evaluation and casts the result to a specific data type.
	 * Logs if the value is empty or can't be cast to the target type.
	 * @param line Index of the line where this evaluation appears
	 * @param default Default value to yield as a replacement for an unexpected result (call-by-name)
	 * @param loggingDetails Details to add to the logging entry (optional, call-by-name)
	 * @param convert A function that converts the acquired result into the targeted type.
	 *                Yields None if the targeted type can't be acquired.
	 * @param context Implicit test context
	 * @tparam A Type of the expected result
	 * @return Cast value or the default value
	 */
	def runExpecting[A](line: Int, default: => A, loggingDetails: (String, Value)*)
	                   (convert: Value => Option[A])
	                   (implicit context: TestContext) =
	{
		val value = run()
		convert(value).getOrElse {
			// Case: Didn't acquire an expected value => Logs and returns the default value
			context.scribe.in("Evaluation.unexpectedResult").warning("Unexpected evaluation result",
				details = Model.from(
					"line" -> line, "eval" -> toString,
					"acquiredValue" -> (if (value.nonEmpty && sensitive) "<omitted>" else value.valueDescription)
				) ++ loggingDetails.iterator.map { case (k, v) => Constant(k, v) }
			)
			default
		}
	}
	
	/**
	 * @param property Targeted property name
	 * @return Access to this result's specified property
	 */
	def get(property: String) = ReadProperty(this, property)
}
