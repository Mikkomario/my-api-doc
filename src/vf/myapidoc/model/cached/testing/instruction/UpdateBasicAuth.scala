package vf.myapidoc.model.cached.testing.instruction

import utopia.flow.collection.immutable.Pair
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.cached.testing.evaluation.TestEvaluation
import vf.myapidoc.model.error.TestFailedException

import scala.util.{Failure, Success}

object UpdateBasicAuth
{
	def apply(parts: Pair[TestEvaluation]): UpdateBasicAuth = apply(parts.first, parts.second)
}

/**
 * Updates the test context to use basic authorization
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
case class UpdateBasicAuth(getUser: TestEvaluation, getPassword: TestEvaluation, line: Int) extends TestInstruction
{
	override def toString = s"Basic authorization = `$getUser` + `$getPassword`"
	
	override def execute()(implicit context: TestContext) = getUser.run().tryString match {
		case Success(user) =>
			getPassword.run().tryString match {
				case Success(password) =>
					context.setBasicAuth(user, password)
					context.log(s"Specified basic authorization using `$getUser` and `$getPassword`")
					
				case Failure(error) =>
					throw new TestFailedException(
						s"Couldn't resolve password for basic auth using: $getPassword", line, cause = error)
			}
		case Failure(error) =>
			throw new TestFailedException(s"Couldn't resolve user for basic auth using: $getUser", line, cause = error)
	}
}
