package vf.myapidoc.controller.console

import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.scribe.core.controller.logging.Scribe
import vf.myapidoc.util.Common._

/**
 * Provides console commands for executing tests
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
object TestCommands
{
	// ATTRIBUTES   -------------------------
	
	private implicit val scribe: Scribe = newScribe("console.Commands.Test")
	
	private val testC = Command("test", help = "Enters the test console or runs a single test")(
		ArgumentSchema("api-name", "api", help = "Name of the API to test", defaultDescription = "Selected"),
		ArgumentSchema("api-path", "url", help = "URL to the tested API root node",
			defaultDescription = "Selected or requested"),
		ArgumentSchema("tests-path", "path", help = "Path to the file that contains the test code",
			defaultDescription = "Selected"),
		ArgumentSchema("test", help = "Name of the test to run", defaultDescription = "Opens the console instead")) {
		args =>
			cPool.logging { implicit c => resolveApi(args("api-name")) }.flatten.foreach { api =>
				// TODO: Implement once we have the missing data structures
				???
			}
	}
}
