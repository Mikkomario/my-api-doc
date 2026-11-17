package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.util.StringExtensions._
import vf.myapidoc.controller.testing.TestContext

/**
 * Persists one or more variables, making them available in consecutive sessions.
 * @author Mikko Hilpinen
 * @since 09.11.2026, v0.1
 */
case class Persist(properties: Seq[String]) extends TestInstruction
{
	override def execute()(implicit context: TestContext): Unit = {
		context.log(s"Persists ${ properties.iterator.map { _.surroundedWith("`") }.mkString(" and ") }")
		properties.foreach(context.persist)
	}
}
