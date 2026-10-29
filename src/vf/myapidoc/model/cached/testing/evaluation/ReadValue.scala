package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import vf.myapidoc.controller.testing.TestContext

/**
 * Reads a value from the context
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ReadValue(key: String) extends TestEvaluation
{
	override def toString = s"`$key`"
	
	override def run()(implicit context: TestContext): Value = context(key)
}
