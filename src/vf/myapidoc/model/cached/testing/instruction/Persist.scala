package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext

/**
 * Persists one or more variables, making them available in consecutive sessions.
 * @author Mikko Hilpinen
 * @since 09.11.2026, v0.1
 */
case class Persist(properties: Seq[String]) extends TestInstruction
{
	override def execute()(implicit context: TestContext): Unit = properties.foreach(context.persist)
}
