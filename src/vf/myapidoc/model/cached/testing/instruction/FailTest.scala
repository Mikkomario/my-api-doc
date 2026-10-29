package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

/**
 * An instruction that fails the currently running test
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class FailTest(message: String = "") extends TestInstruction
{
	override def execute()(implicit context: TestContext) = {
		context.log(if (message.isEmpty) "Failing test" else s"Failing test: $message")
		throw new TestFailedException(message.nonEmptyOrElse("no message"))
	}
}
