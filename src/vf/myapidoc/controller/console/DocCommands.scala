package vf.myapidoc.controller.console

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.result.TryCatch
import utopia.flow.view.template.Extender
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.model.enumeration.Severity.Recoverable
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import vf.myapidoc.controller.doc.DocParser
import vf.myapidoc.database.store.ApiDocDb
import vf.myapidoc.model.cached.{NewApiNodeGroup, NewNamedModel}
import vf.myapidoc.util.Common._

import java.nio.file.Path
import scala.io.StdIn

/**
 * Introduces console commands for interacting with API documentation
 * @author Mikko Hilpinen
 * @since 25.10.2026, v0.1
 */
object DocCommands extends Extender[(String, Seq[Command])]
{
	// ATTRIBUTES   ---------------------------
	
	private implicit val scribe: Scribe = newScribe("console.Commands.Doc")
	
	/**
	 * A console command for updating API documentation from md files.
	 */
	private val update = Command("update", help = "Updates API documentation")(
		ArgumentSchema("api-name", "name", help = "Name of the API", defaultDescription = "Requested"),
		ArgumentSchema("api-directory", "dir", help = "Directory that contains the API documentation",
			defaultDescription = "Requested")) {
		args =>
			// Finds the API directory
			resolvePath(args("api-directory"),
				prompt = "Please specify the directory that contains the API documentation", targetDirectories = true,
				filterBasedOnContent = true) { _.fileType ~== "md" }
				.foreach { apiDir =>
					val possibleFiles = apiDir.iterateChildren { _.filter { _.fileType ~== "md" }.toOptimizedSeq }
						.logWith("Failed to scan the API directory", subContext = "update.resolve.paths.scan")
						.getOrElse(Empty)
					
					// Case: No suitable files found (unexpected) => Fails
					if (possibleFiles.isEmpty)
						println(s"No API documentation files were found from ${ apiDir.toAbsolutePath }")
					else {
						// Asks the user to specify the API node documentation file(s)
						println("Which of the following files contains API node documentation?")
						val nodeFiles = StdIn.selectMany(possibleFiles.map { p => p -> p.fileName }, "files").toSet
						// Case: No files selected => Cancels
						if (nodeFiles.isEmpty)
							println("No files were selected")
						else {
							// Asks the user to select the API model documentation file(s)
							val modelFiles = NotEmpty(possibleFiles.filterNot(nodeFiles.contains)) match {
								case Some(possibleFiles) =>
									println("Which of the following files, if any, contains API model documentation?")
									StdIn.selectMany(possibleFiles.map { p => p -> p.fileName }, "files")
									
								// Case: No more selectable files => Skips this part
								case None => Empty
							}
							// Parses the API documentation from the selected files
							parseApiDoc(nodeFiles, modelFiles).foreach { case (nodes, models) =>
								// Prints the parsed information as JSON
								println(s"Parsed ${ nodes.iterator.map { _.nodes.size }.sum } API nodes:")
								nodes.foreach { group => println(group.toJson) }
								
								if (models.nonEmpty) {
									println(s"\nParsed ${ models.size } named model types:")
									models.foreach { model => println(model.toJson) }
								}
								
								// Verifies the output with the user
								if (StdIn.ask("Should we proceed to store this information?", default = true))
									args("api-name").string
										// Asks for the API name if not already provided
										.orElse { StdIn.readNonEmptyLine("Enter the name of this API") }
										.foreach { apiName =>
											cPool.logging { implicit c =>
												// Stores the API documentation in the database
												val (api, missingModelRefs) = ApiDocDb.storeApi(apiName, models, nodes)
												if (api.isNew)
													println("Stored a new API")
												else
													println("Updated the API")
												
												if (missingModelRefs.nonEmpty) {
													println("Warning: The following model type references couldn't be resolved:")
													missingModelRefs.foreach { ref => println(s"- $ref") }
												}
												
												// Optionally prints the current API doc JSON
												if (StdIn.ask("Do you want to print the current API doc JSON?",
													default = true))
													println(ApiDocDb.pullDetailsFor(api).toJson)
											}
										}
							}
						}
					}
				}
	}
	
	
	// IMPLEMENTED  ---------------------
	
	override def wrapped: (String, Seq[Command]) = "doc" -> Single(update)
	
	
	// OTHER    -------------------------
	
	// Attempts to parse the node files first
	private def parseApiDoc(nodeFiles: Iterable[Path], modelFiles: Iterable[Path]) =
		nodeFiles.tryFlatMapEach { DocParser.readApiNodesFrom(_) } match {
			case TryCatch.Success(nodes, partialFailures) =>
				// If there were partial failures, logs them and asks the user whether we should continue
				val shouldContinue = {
					if (partialFailures.isEmpty)
						true
					else {
						partialFailures.logWith("Failed to parse some of the API nodes documentation",
							subContext = "parse.apiDoc.nodeFiles.partialFailures", severity = Recoverable)
						StdIn.ask(s"Failed to read ${ partialFailures.size } of ${
							nodeFiles.size } API node files. Should we continue anyway?")
					}
				}
				if (shouldContinue) {
					// Parses the model files, if applicable
					val modelFilesResult = modelFiles.tryFlatMapEach(DocParser.readModelsFrom)
					modelFilesResult.logWith("Failure(s) while parsing API model documentation",
						subContext = "parse.apiDoc.modelFiles")
					
					// If there were failures involved, asks the user before continuing
					// Case: No failures or the user chose to continue => Yields the parsed nodes and models
					if (modelFilesResult.isFullSuccess ||
						StdIn.ask(s"Failed to read${ if (modelFilesResult.isPartialFailure) " some of" else "" } the API model files. Should we continue anyway?"))
						Some[(Iterable[NewApiNodeGroup], Iterable[NewNamedModel])](
							nodes -> modelFilesResult.success.getOrElse(Empty))
					// Case: User chose not to continue => Yields None
					else
						None
				}
				// Case: User chose not to continue => Yields None
				else
					None
				
			// Case: Failure => Logs and yields None
			case TryCatch.Failure(error) =>
				scribe.in("parse.apiDoc.nodeFiles")(error, "Couldn't parse API nodes documentation")
				println("Couldn't parse the API nodes documentation. See the logged error.")
				None
		}
}
