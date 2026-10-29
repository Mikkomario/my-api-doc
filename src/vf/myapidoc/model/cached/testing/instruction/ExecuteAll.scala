package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext

/**
 * Executes multiple instructions sequentially
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteAll(instructions: Seq[TestInstruction]) extends TestInstruction
{
	// IMPLEMENTED  ---------------------------
	
	override def toString = instructions.headOption match {
		case Some(first) => s"`$first` and then ${ instructions.size - 1 } more"
		case None => "nothing"
	}
	
	override def execute()(implicit context: TestContext) = instructions.foreach { _.execute() }
	
	override def +(other: TestInstruction) = ExecuteAll(instructions :+ other)
	override def +:(previous: TestInstruction) = ExecuteAll(previous +: instructions)
	override def ++(other: IterableOnce[TestInstruction]) = ExecuteAll(instructions ++ other)
}
