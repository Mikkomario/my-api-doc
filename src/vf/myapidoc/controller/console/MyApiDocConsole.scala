package vf.myapidoc.controller.console

import utopia.flow.async.context.CloseHook
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.AppConfig
import utopia.flow.util.console.Console
import utopia.flow.util.result.TryCatch
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.model.enumeration.Severity.Recoverable
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import utopia.vault.database.Connection
import vf.myapidoc.util.Common._

import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * Console application for this project
 * @author Mikko Hilpinen
 * @since 26.10.2026, v0.1
 */
object MyApiDocConsole extends App
{
	// ATTRIBUTES	---------------------
	
	private implicit val scribe: Scribe = newScribe("Console")
	
	
	// APP CODE -------------------------
	
	private val wasSuccess = AppConfig("myapidoc", allowWorkingDirectoryAsAppDirectory = true) match {
		case TryCatch.Success(config, partialFailures) =>
			partialFailures.logWith("Partial failures while setting up app-configuration",
				subContext = "init.appConfig.partialFailures", severity = Recoverable)
			
			if (setupDbAccess(config)) {
				val console = Console.staticNamespaced(Map(DocCommands), "\nPlease specify the next command: ", "exit",
					listAvailableCommands = true)
				console.run()
				true
			}
			else
				false
			
		case TryCatch.Failure(error) =>
			scribe.in("init.appConfig")(error, "Couldn't set up app-configuration")
			false
	}
	
	CloseHook.maxShutdownTime = 25.seconds
	CloseHook.shutdown()
	println("Bye!")
	System.exit(if (wasSuccess) 0 else 1)
	
	
	// OTHER    -------------------------
	
	private def setupDbAccess(config: AppConfig) = {
		var updated = false
		val user = config("user").stringOr {
			updated = true
			print("Please specify the user for accessing the database: ")
			StdIn.readLine()
		}
		val password = config("password").stringOr {
			updated = true
			print("Please specify the password for accessing the database: ")
			StdIn.readLine()
		}
		
		Connection.modifySettings { _.copy(
			user = user, password = password, charsetName = "utf8", charsetCollationName = "utf8_general_ci") }
		
		// Tests the DB connection
		println("Testing DB connection...")
		cPool { _.test() } match {
			case Success(_) =>
				println("Connection successful!")
				if (updated) {
					config("user") = user
					config("password") = password
				}
				true
			case Failure(error) =>
				scribe.in("init.dbAccess")(error, "Can't access the database")
				config("user") = Value.empty
				config("password") = Value.empty
				println("Connection failed!")
				false
		}
	}
}
