package vf.myapidoc.database

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.AppConfig
import utopia.flow.util.result.TryCatch
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.model.enumeration.Severity.Recoverable
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import utopia.vault.database.Connection
import utopia.vault.database.columnlength.ColumnLengthRules
import vf.myapidoc.util.Common._

import java.nio.file.Path
import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * An interface for setting up DB access
 * @author Mikko Hilpinen
 * @since 28.10.2026, v0.1
 */
object SetupDb
{
	// ATTRIBUTES    -----------------------------
	
	private implicit val scribe: Scribe = newScribe("DB.Setup")
	
	
	// OTHER    ----------------------------------
	
	/**
	 * Sets up DB connection
	 * @param enableUserInteraction Whether user-interaction should be enabled. Default = false.
	 * @return
	 */
	def apply(enableUserInteraction: Boolean = false) =
		AppConfig("myapidoc", allowWorkingDirectoryAsAppDirectory = true) match {
			case TryCatch.Success(config, partialFailures) =>
				partialFailures.logWith("Partial failures while setting up app-configuration",
					subContext = "appConfig.partialFailures", severity = Recoverable)
				
				var updated = false
				val user = config("user").stringOr {
					updated = true
					if (enableUserInteraction) {
						print("Please specify the user for accessing the database: ")
						StdIn.readLine()
					}
					else
						"root"
				}
				val password = config("password").stringOr {
					if (enableUserInteraction) {
						updated = true
						print("Please specify the password for accessing the database: ")
						StdIn.readLine()
					}
					else
						""
				}
				
				Connection.modifySettings { _.copy(
					user = user, password = password, charsetName = "utf8", charsetCollationName = "utf8_general_ci") }
				
				// Tests the DB connection
				if (enableUserInteraction)
					println("Testing DB connection...")
				cPool { _.test() } match {
					case Success(_) =>
						if (enableUserInteraction)
							println("Connection successful!")
						if (updated) {
							config("user") = user
							config("password") = password
						}
						
						// Loads column-length rules
						val columnLengthRulesPath: Path = "data/length-rules"
						if (columnLengthRulesPath.exists)
							ColumnLengthRules.loadFrom(columnLengthRulesPath, dbName)
						else
							scribe.in("columnLengthRules").recoverable("Failed to load column-length rules")
						
						true
					case Failure(error) =>
						scribe(error, "Can't access the database")
						config("user") = Value.empty
						config("password") = Value.empty
						if (enableUserInteraction)
							println("Connection failed!")
						false
				}
			
			case TryCatch.Failure(error) =>
				scribe.in("appConfig")(error, "Couldn't set up app-configuration")
				false
		}
}
