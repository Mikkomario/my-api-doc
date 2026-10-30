package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.LastResponseValue.sensitiveKeys

object LastResponseValue
{
	private val sensitiveKeys = Set("password", "token", "secret")
}

/**
 * Reads a value from the last executed request
 * @param key Key read from the response body
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class LastResponseValue(key: String) extends TestEvaluation
{
	// ATTRIBUTES   ----------------------
	
	private val keyOrPath = key.split(Regex.dot).oneOrMany
	private val alwaysSensitive = keyOrPath match {
		case Left(key) => sensitiveKeys.contains(key.toLowerCase)
		case Right(path) => path.exists { p => sensitiveKeys.contains(p.toLowerCase) }
	}
	
	
	// IMPLEMENTED  ----------------------
	
	override def toString = s"last.$key"
	
	override def sensitive(implicit context: TestContext): Boolean = alwaysSensitive || (keyOrPath match {
		case Left(key) => context.isSensitive(key)
		case Right(path) => path.exists(context.isSensitive)
	})
	
	override def run()(implicit context: TestContext): Value = {
		// Primarily attempts to read a value from the response body
		val valueFromContext = keyOrPath match {
			case Left(key) => context.lastResponseBody(key)
			case Right(path) => context.lastResponseBody.getModel.nested(path)
		}
		// Case: Value found => Yields it
		if (valueFromContext.nonEmpty)
			valueFromContext
		// Case: No value was read => Checks for custom keys
		else
			key.toLowerCase match {
				case "status" | "statuscode" | "status.code" => context.lastResponseStatus.map { _.code }
				case "body" | "value" => context.lastResponseBody
				case "succeeded" | "success" => context.lastResponseStatus.exists { _.isSuccess }
				case "failed" | "failure" => context.lastResponseStatus.forall { _.isFailure }
				case "isempty" | "empty" => context.lastResponseBody.isEmpty
				case "size" | "count" => context.lastResponseBody.getVector.size
				// Case: No custom key => Yields an empty value
				case _ => valueFromContext
			}
	}
}