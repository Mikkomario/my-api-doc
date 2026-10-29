package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Used for passing hard-coded values to test instructions
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class HardCodedValue(value: Value) extends TestEvaluation
{
	override def toString = value.toJson
	
	override def run()(implicit context: TestContext): Value = value
}
