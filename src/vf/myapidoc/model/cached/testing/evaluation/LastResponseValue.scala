package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Empty
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
	private val (primaryKey, remainingPath) = keyOrPath match {
		case Left(key) => key.toLowerCase -> Empty
		case Right(path) => path.head.toLowerCase -> path.tail
	}
	
	private val alwaysSensitive = keyOrPath match {
		case Left(key) => sensitiveKeys.contains(key.toLowerCase)
		case Right(path) => path.exists { p => sensitiveKeys.contains(p.toLowerCase) }
	}
	
	private val delegate = keyOrPath match {
		case Left(key) => ReadProperty(LastResponseBody, key)
		case Right(path) => path.foldLeft(LastResponseBody: TestEvaluation) { _ get _ }
	}
	
	
	// IMPLEMENTED  ----------------------
	
	override def toString = s"last.$key"
	
	override def sensitive(implicit context: TestContext): Boolean = alwaysSensitive || (keyOrPath match {
		case Left(key) => context.isSensitive(key)
		case Right(path) => path.exists(context.isSensitive)
	})
	
	override def run()(implicit context: TestContext): Value = {
		// Primarily attempts to read a value from the response body
		val valueFromDelegate = delegate.run()
		// Case: Value found => Yields it
		if (valueFromDelegate.nonEmpty)
			valueFromDelegate
		// Case: No value was read => Checks for custom keys
		else {
			val customValue: Option[Value] = primaryKey match {
				case "status" | "statuscode" | "status.code" => Some(context.lastResponseStatus.map { _.code })
				case "body" | "value" => Some(context.lastResponseBody)
				case "succeeded" | "success" => Some(context.lastResponseStatus.exists { _.isSuccess })
				case "failed" | "failure" => Some(context.lastResponseStatus.forall { _.isFailure })
				// Case: No custom key
				case _ => None
			}
			customValue match {
				// Case: Custom key targeted => Resolves the remaining path, if appropriate
				case Some(value) =>
					if (remainingPath.isEmpty || value.isEmpty)
						value
					else
						remainingPath.foldLeft(HardCodedValue(value): TestEvaluation) { _ get _ }.run()
					
				// Case: No custom key => Yields the value from the delegate
				case None => valueFromDelegate
			}
		}
	}
}