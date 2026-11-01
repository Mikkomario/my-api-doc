package vf.myapidoc.controller.console

import utopia.flow.async.context.CloseHook
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.console.Console
import vf.myapidoc.database.SetupDb
import vf.myapidoc.util.Common._

import scala.util.{Failure, Success, Try}

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
			val commandsP = TestCommands.pointer.map { testCommands => Map(DocCommands, "test" -> testCommands) }
			val console = Console.namespaced(commandsP, "\nPlease specify the next command: ",
				closeCommandName = "exit", listAvailableCommands = true)
			Try { console.run() } match {
				case Success(_) => true
				case Failure(error) =>
					newScribe("Console")(error, "Unexpected failure while running the console")
					false
			}
		}
		else
			false
	}
	
	CloseHook.maxShutdownTime = 25.seconds
	CloseHook.shutdown()
	println("Bye!")
	System.exit(if (wasSuccess) 0 else 1)
}
