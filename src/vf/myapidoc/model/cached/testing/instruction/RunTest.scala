package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

/**
 * An instruction to run a specific test
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class RunTest(testName: String, line: Int) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = context.getTest(testName) match {
		case Some(test) =>
			context.log(s"Calling test \"${ test.name }\"")
			try { test.run(nested = true) } catch {
				case e: Exception =>
					throw new TestFailedException(s"Failed to run nested test: \"$testName\"", line, cause = e)
			}
		case None =>
			val message = s"Attempted to run a non-existing test: \"$testName\""
			context.log(s"Failure: $message")
			throw new TestFailedException(message, line)
	}
}
