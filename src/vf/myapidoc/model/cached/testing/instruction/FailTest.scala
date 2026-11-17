package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.{HardCodedValue, TestEvaluation}
import vf.myapidoc.model.error.TestFailedException

/**
 * An instruction that fails the currently running test
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class FailTest(line: Int, getMessage: TestEvaluation = HardCodedValue.empty) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = {
		val message = getMessage.run().getString
		context.log(if (message.isEmpty) s"Failing test on line $line" else s"Failing test: $line: $message")
		throw new TestFailedException(message.nonEmptyOrElse("no message"), line)
	}
}
