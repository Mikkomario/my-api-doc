package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

/**
 * Asserts that a certain test evaluation holds true
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class TestAssertion(evaluation: TestEvaluation) extends TestInstruction
{
	override def toString = s"Assert `$evaluation`"
	
	override def execute()(implicit context: TestContext) = {
		val result = evaluation.run()
		if (!result.getBoolean) {
			val message = s"Assertion failed: Expected `$evaluation`, got $result"
			context.log(message)
			throw new TestFailedException(message)
		}
	}
}
