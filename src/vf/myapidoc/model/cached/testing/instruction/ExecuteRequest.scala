package vf.myapidoc.model.cached.testing.instruction

import utopia.access.model.enumeration.Method
import utopia.flow.util.EitherExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.combined.api.DetailedApiNode

/**
 * Executes an API request
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteRequest(method: Method, pathOrNode: Either[String, DetailedApiNode]) extends TestInstruction
{
	override def toString = s"$method ${ pathOrNode.leftOrMap { _.absolutePath } }"
	
	override def execute()(implicit context: TestContext) =
		context.request(method, pathOrNode)
}
