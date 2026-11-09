package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.Now
import vf.myapidoc.controller.testing.TestContext

/**
 * An evaluation that resolves into the current timestamp
 * @author Mikko Hilpinen
 * @since 09.11.2026, v0.1
 */
object CurrentTimestamp extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = false
	
	override def run()(implicit context: TestContext): Value = Now
}
