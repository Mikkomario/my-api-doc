package vf.myapidoc.model.cached.testing.evaluation

import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.console.ConsoleExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.util.Common.jsonParser

import scala.io.StdIn

/**
 * A test code for requesting for user input
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class PromptForValue(getPrompt: TestEvaluation, hide: Boolean = false) extends TestEvaluation
{
	// IMPLEMENTED  ----------------------
	
	override def toString = s"prompt: `$getPrompt`"
	
	override def sensitive(implicit context: TestContext): Boolean = hide || getPrompt.sensitive
	
	override def run()(implicit context: TestContext): Value = getPrompt.run().string match {
		case Some(prompt) =>
			StdIn.readNonEmptyLine(prompt) match {
				case Some(input) =>
					context.log(s"User passed `${ if (hide) input.map { _ => '*' } else input }`")
					jsonParser.valueOf(input)
				
				case None =>
					context.log("User did not pass anything")
					Value.empty
			}
		case None =>
			context.log(s"Prompt `$getPrompt` skipped")
			Value.empty
	}
}
