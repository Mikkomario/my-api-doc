package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * Conditionally executes some code
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteIf(condition: TestEvaluation, code: TestInstruction, alternative: Option[TestInstruction] = None)
	extends TestInstruction
{
	override def toString = alternative match {
		case Some(alt) => s"`$code` if `$condition` else `$alt`"
		case None => s"`$code` if `$condition`"
	}
	
	override def execute()(implicit context: TestContext) = {
		if (condition.run().getBoolean) {
			context.log(s"Entered `$condition`")
			code.execute()
		}
		else
			alternative match {
				case Some(alt) =>
					context.log(s"Skipped `$condition` => Running else")
					alt.execute()
				case None => context.log(s"Skipped `$condition`")
			}
	}
}
