package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.model.enumeration.ValueOperation2
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.operation.ValueOperator
import vf.myapidoc.controller.testing.TestContext

/**
 * Combines two values together
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class Combine(values: Pair[TestEvaluation], operation: ValueOperation2) extends TestEvaluation
{
	// IMPLEMENTED	--------------------
	
	override def sensitive(implicit context: TestContext): Boolean = values.exists { _.sensitive }
	
	override def run()(implicit context: TestContext): Value =
		values.mapAndMerge { _.run() } { (a, b) => ValueOperator(a, operation, b) }
}
