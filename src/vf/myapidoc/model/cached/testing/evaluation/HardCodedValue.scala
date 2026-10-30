package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

object HardCodedValue
{
	// ATTRIBUTES	----------------------
	
	val empty = HardCodedValue(Value.empty)
	val trueValue = HardCodedValue(true)
	val falseValue = HardCodedValue(false)
	
	
	// OTHER    --------------------------
	
	def string(value: String): HardCodedValue = apply(value: Value)
}

/**
 * Used for passing hard-coded values to test instructions
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class HardCodedValue(value: Value) extends TestEvaluation
{
	// IMPLEMENTED  ---------------------
	
	override def toString = value.toJson
	
	override def sensitive(implicit context: TestContext): Boolean = false
	
	override def run()(implicit context: TestContext): Value = value
}
