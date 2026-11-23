package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.operator.enumeration.LogicalOperator
import utopia.flow.util.UncertainBoolean
import vf.myapidoc.controller.testing.TestContext

/**
 * Evaluates two values and joins them logically
 * @author Mikko Hilpinen
 * @since 15.11.2026, v0.1
 */
case class LogicalOperation(parts: Pair[TestEvaluation], operator: LogicalOperator) extends TestEvaluation
{
	// IMPLEMENTED  -------------------------
	
	override def toString: String = s"${parts.first} $operator ${parts.second}"
	
	override def sensitive(implicit context: TestContext): Boolean = parts.exists { _.sensitive }
	
	override def run()(implicit context: TestContext): Value = {
		val values = parts.view.map[UncertainBoolean] { eval =>
			val value = eval.run()
			val result = value.boolean
			if (result.isEmpty) {
				if (value.isEmpty)
					context.scribe.in("Operation.Logical.run.nullValue").recoverable("Empty input value encountered",
						Model.from("eval" -> eval.toString))
				else
					context.scribe.in("Operation.Logical.run.cannotCast").recoverable(
						"Cannot cast the value to boolean", Model.from("eval" -> eval.toString, "value" -> value))
			}
			result
		}
		operator(values.first, values.second)
	}
}
