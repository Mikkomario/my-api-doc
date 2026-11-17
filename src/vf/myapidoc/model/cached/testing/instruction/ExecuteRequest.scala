package vf.myapidoc.model.cached.testing.instruction

import utopia.access.model.enumeration.Method
import utopia.flow.util.EitherExtensions._
import vf.myapidoc.controller.testing.TestContext
import vf.myapidoc.model.combined.api.DetailedApiNode
import vf.myapidoc.model.error.TestFailedException

/**
 * Executes an API request
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
case class ExecuteRequest(method: Method, pathOrNode: Either[String, DetailedApiNode], line: Int)
	extends TestInstruction
{
	// ATTRIBUTES   ------------------
	
	private lazy val path = pathOrNode.leftOrMap {
		_.absolutePaths.headOption match {
			case Some((path, _)) => path
			case None => "???"
		}
	}
	
	
	// IMPLEMENTED  ------------------
	
	override def toString = s"$method $path"
	
	override def execute()(implicit context: TestContext) =
		try { context.request(method, pathOrNode, line) } catch {
			case e: Exception => throw new TestFailedException(s"Failed to execute $method $path", line, cause = e)
		}
}
