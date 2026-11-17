package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation
import vf.myapidoc.model.error.TestFailedException

/**
 * Loops a code until an evaluation is resolved
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteWhile(condition: TestEvaluation, code: TestInstruction, line: Int) extends TestInstruction
{
	// IMPLEMENTED  ---------------------
	
	override def execute()(implicit context: TestContext) = {
		var loops = 0
		while (loops < 1000 && condition.runExpectingBoolean(line, "loops" -> loops)) {
			if (loops > 0)
				context.log("Loops")
			loops += 1
			code.execute()
		}
		if (loops >= 1000)
			throw new TestFailedException(s"Looped `while ($condition) $code` too many times", line)
	}
}
