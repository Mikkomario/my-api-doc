package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext

object HardCodedValue
{
	val empty = HardCodedValue(Value.empty)
	val trueValue = HardCodedValue(true)
	val falseValue = HardCodedValue(false)
}

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
