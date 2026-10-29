package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext

/**
 * Logs a comment when running a test
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class PrintComment(comment: String) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = context.log(comment)
}
