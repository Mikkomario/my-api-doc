package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.ComparisonOperator.Equality

/**
 * Compares two values with each other
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class Compare(values: Pair[TestEvaluation], operator: ComparisonOperator = Equality) extends TestEvaluation
{
	override def toString = s"${ values.first } $operator ${ values.second }"
	
	override def run()(implicit context: TestContext): Value = operator(values.map { _.run() })
}
