package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.async.process.Wait
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation
import vf.myapidoc.model.error.TestFailedException
import vf.myapidoc.util.Common._

import scala.util.{Failure, Success}

/**
 * An instruction to wait for a moment before continuing to the next instruction.
 * @author Mikko Hilpinen
 * @since 01.11.2026, v0.1
 */
case class ProceedAfterWait(getDuration: TestEvaluation) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = getDuration.run().tryDuration match {
		case Success(duration) =>
			if (duration.isPositive) {
				context.log(s"Waiting for ${ duration.description }")
				if (!Wait(duration))
					throw new TestFailedException("Test was interrupted")
			}
		case Failure(error) => throw new IllegalArgumentException("Failed to acquire the wait duration", error)
	}
}
