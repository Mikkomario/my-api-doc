package vf.myapidoc.controller.console

import utopia.flow.async.context.CloseHook
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.console.Console
import vf.myapidoc.database.SetupDb
import vf.myapidoc.util.Common._

/**
 * Console application for this project
 * @author Mikko Hilpinen
 * @since 26.10.2026, v0.1
 */
object MyApiDocConsole extends App
{
	// APP CODE -------------------------
	
	private val wasSuccess = {
		if (SetupDb(enableUserInteraction = true)) {
			val console = Console.staticNamespaced(Map(DocCommands), "\nPlease specify the next command: ", "exit",
				listAvailableCommands = true)
			console.run()
			true
		}
		else
			false
	}
	
	CloseHook.maxShutdownTime = 25.seconds
	CloseHook.shutdown()
	println("Bye!")
	System.exit(if (wasSuccess) 0 else 1)
}
