package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Yields the body of the last received response
 * @author Mikko Hilpinen
 * @since 11.11.2026, v0.1
 */
object LastResponseBody extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = false
	
	override def run()(implicit context: TestContext): Value = context.lastResponseBody
}
