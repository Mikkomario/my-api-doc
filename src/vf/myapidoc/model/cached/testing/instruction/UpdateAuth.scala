package vf.myapidoc.model.cached.testing.instruction

import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation

/**
 * A test instruction for changing the authorization method
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class UpdateAuth(getAuth: TestEvaluation) extends TestInstruction
{
	override def toString = s"Authorization = `$getAuth`"
	
	override def execute()(implicit context: TestContext) = {
		context.setAuth(getAuth.run().getString)
		context.log(s"Specified authorization using `$getAuth`")
	}
}
