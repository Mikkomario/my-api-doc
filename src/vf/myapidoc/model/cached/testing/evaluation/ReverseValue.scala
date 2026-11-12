package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Reverses / inverts the result of another evaluation
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class ReverseValue(value: TestEvaluation) extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = value.sensitive
	
	override def run()(implicit context: TestContext): Value = -value.run()
}
