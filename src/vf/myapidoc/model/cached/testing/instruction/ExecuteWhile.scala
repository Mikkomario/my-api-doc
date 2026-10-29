package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

/**
 * Loops a code until an evaluation is resolved
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteWhile(condition: TestEvaluation, code: TestInstruction) extends TestInstruction
{
	override def execute()(implicit context: TestContext) = {
		var loops = 0
		while (loops < 1000 && condition.run().getBoolean) {
			if (loops > 0)
				context.log("Loops")
			loops += 1
			code.execute()
		}
		if (loops >= 1000)
			throw new TestFailedException(s"Looped `while ($condition) $code` too many times")
	}
}
