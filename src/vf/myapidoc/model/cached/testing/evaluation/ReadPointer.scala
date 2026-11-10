package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.view.immutable.View
import vf.myapidoc.controller.testing.TestContext

/**
 * An evaluation that reads an external pointer
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class ReadPointer(pointer: View[Value]) extends TestEvaluation
{
	override def sensitive(implicit context: TestContext): Boolean = false
	
	override def run()(implicit context: TestContext): Value = pointer.value
}
