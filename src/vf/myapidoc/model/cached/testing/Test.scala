package vf.myapidoc.model.cached.testing

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.util.result.TryExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.instruction.TestInstruction
import vf.myapidoc.model.error.SyntaxException

import scala.util.Try

/**
 * Represents a test that may be run
 * @param name Name of this test
 * @param code Code to execute in this test
 * @param description A description of this test
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
case class Test(name: String, code: TestInstruction, description: String = "")
{
	/**
	 * Runs this test
	 * @param context Implicit test context
	 * @return Whether this test succeeded
	 */
	def run()(implicit context: TestContext) =
		Try
			.apply {
				val descPart = if (description.isEmpty) "" else s" ($description)"
				context.log(s"\nRunning $name$descPart")
				code.execute()
				context.log(s"$name succeeded")
				true
			}
			.getOrMap {
				case e: SyntaxException =>
					context.scribe(e, "Test failed", Model.from("test" -> name, "line" -> e.line))
					false
					
				case e =>
					context.scribe(e, "Unexpected failure while running a test", Model.from("test" -> name))
					false
			}
}
