package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Prints a value to the console and test logs
 * @author Mikko Hilpinen
 * @since 10.11.2026, v0.1
 */
case class PrintValue(getValue: TestEvaluation) extends TestInstruction
{
	override def execute()(implicit context: TestContext): Unit =
		context.printValue(getValue.run(), skipLog = getValue.sensitive)
}