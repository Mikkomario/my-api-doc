package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.operator.enumeration.LogicalOperator
import vf.myapidoc.controller.testing.TestContext

/**
 * Evaluates two values and joins them logically
 * @author Mikko Hilpinen
 * @since 15.11.2026, v0.1
 */
case class LogicalOperation(parts: Pair[TestEvaluation], operator: LogicalOperator) extends TestEvaluation
{
	override def toString: String = s"${parts.first} $operator ${parts.second}"
	
	override def sensitive(implicit context: TestContext): Boolean = parts.exists { _.sensitive }
	
	override def run()(implicit context: TestContext): Value = {
		parts.findForBoth { _.run().boolean } match {
			case Some(values) => operator(values.first, values.second)
			case None =>
				context.scribe.in("Operation.Logical.run").warning(
					"Empty or non-boolean values were involved in a logical operation",
					Model.from("parts" -> parts.map { _.toString }))
				Value.empty
		}
	}
}
