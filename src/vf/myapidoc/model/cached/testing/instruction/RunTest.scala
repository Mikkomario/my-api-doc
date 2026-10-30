package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext

/**
 * An instruction to run a specific test
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class RunTest(testName: String) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = context.run(testName)
}
