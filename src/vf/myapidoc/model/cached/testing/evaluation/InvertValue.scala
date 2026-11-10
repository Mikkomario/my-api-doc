package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext

/**
 * Inverts a (boolean) value
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class InvertValue(value: TestEvaluation) extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = value.sensitive
	
	override def run()(implicit context: TestContext): Value = {
		val _value = value.run()
		_value.boolean match {
			case Some(bool) => !bool
			case None =>
				if (_value.nonEmpty)
				    context.scribe.in("Evaluation.Invert")
					    .warning("Could not invert a value", Model.from("value" -> _value))
				_value
		}
	}
}
