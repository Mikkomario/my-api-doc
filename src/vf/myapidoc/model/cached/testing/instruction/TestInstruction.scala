package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.collection.immutable.{OptimizedIndexedSeq, Pair, Single}
import utopia.flow.collection.CollectionExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.error.TestFailedException

object TestInstruction
{
	/**
	 * @return A builder for building an instruction from possibly multiple lines.
	 */
	def newBuilder =
		OptimizedIndexedSeq.newBuilder[TestInstruction].mapResult { _.emptyOneOrMany.map {
			case Left(line) => line
			case Right(lines) => ExecuteAll(lines)
		} }
}

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
