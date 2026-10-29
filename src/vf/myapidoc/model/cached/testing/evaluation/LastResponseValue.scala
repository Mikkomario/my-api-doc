package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Reads a value from the last executed request
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class LastResponseValue(key: String) extends TestEvaluation
{
	override def toString = s"last.$key"
	
	override def run()(implicit context: TestContext): Value = {
		// Primarily attempts to read a value from the response body
		val valueFromContext = context.lastResponseBody(key)
		if (valueFromContext.nonEmpty)
			valueFromContext
		// Case: No value was read => Checks for custom keys
		else
			key.toLowerCase match {
				case "status" | "statuscode" => context.lastResponseStatus.map { _.code }
				case "body" | "value" => context.lastResponseBody
				case "succeeded" | "success" => context.lastResponseStatus.exists { _.isSuccess }
				case "failed" | "failure" => context.lastResponseStatus.forall { _.isFailure }
				case "isempty" | "empty" => context.lastResponseBody.isEmpty
				case "size" | "count" => context.lastResponseBody.getVector.size
				case _ => valueFromContext
			}
	}
}