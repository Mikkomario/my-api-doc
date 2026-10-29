package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.collection.immutable.{Pair, Single}
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

/**
 * Common trait for test code blocks / instructions
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
trait TestInstruction
{
	// ABSTRACT     ---------------------------
	
	/**
	 * Runs this test instruction
	 * @param context Implicit test context
	 * @throws TestFailedException If a test assertion fails
	*/
	@throws[TestFailedException]("If a test assertion fails")
	def execute()(implicit context: TestContext): Unit
	
	
	// OTHER    -------------------------------
	
	def +(other: TestInstruction): TestInstruction = ExecuteAll(Pair(this, other))
	def +:(previous: TestInstruction): TestInstruction = previous + this
	def ++(other: IterableOnce[TestInstruction]): TestInstruction = ExecuteAll(Single(this) ++ other)
}
