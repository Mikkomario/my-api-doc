package vf.myapidoc.controller

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.model.immutable.Value
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.file.FileUtils
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import utopia.vault.database.Connection
import vf.myapidoc.database.access.api.AccessApis
import vf.myapidoc.database.store.ApiDocDb

import java.nio.file.Path
import scala.io.StdIn

/**
 * Provides utility functions for the console classes
 * @author Mikko Hilpinen
 * @since 25.10.2026, v0.1
 */
package object console
{
	/**
	 * Allows the user to select an API
	 * @param arg An argument that refers to the selected API
	 * @param connection Implicit DB connection
	 * @return Selected API, including full details
	 */
	def resolveApi(arg: Value = Value.empty)(implicit connection: Connection) = {
		val options = arg.string match {
			case Some(name) => AccessApis.withNameContaining(name).pull.bestMatch { _.name ~== name }
			case None => AccessApis.pull
		}
		options.only
			.orElse {
				if (options.isEmpty) {
					println("No APIs have been documented yet")
					None
				}
				else {
					println("Which API did you mean?")
					StdIn.selectFrom(options.map { api => api -> api.name })
				}
			}
			.map { ApiDocDb.pullDetailsFor(_) }
	}
	
	/**
	 * Resolves a path together with the user
	 * @param arg Received file (command) argument
	 * @param root The top searched directory. Default = current working directory.
	 * @param prompt Prompt to display before asking the user to select a file or a directory
	 * @param targetDirectories Whether searching for directories.
	 *                          False if regular files are searched (default)
	 * @param fileFilter Filter that only accepts valid files.
	 *
	 *                   Note: If 'targetDirectories' is set to true and 'filterBasedOnContent' is left as false,
	 *                   this filter is applied to directories instead of regular files.
	 *
	 * @param filterBasedOnContent Whether 'fileFilter' should be applied to directory contents.
	 *                             Ignored if 'targetDirectories' is false (default).
	 *                             Default = false = 'fileFilter' will be applied to the targeted directories,
	 *                             if applicable.
	 * @param scribe Implicit scribe interface for logging
	 * @return Selected (existing) regular file path. None if not found or canceled by the user.
	 */
	def resolvePath(arg: Value = Value.empty, root: Path = FileUtils.workingDirectory,
	                prompt: String = "Please specify the targeted file", targetDirectories: Boolean = false,
	                filterBasedOnContent: Boolean = false)
	               (fileFilter: Path => Boolean)(implicit scribe: Scribe) =
	{
		// Finds all directories that may be targeted, including targetable files (if applicable)
		val allDirectoriesIter = {
			val base = root.toTree.topDownNodesIterator.filter { _.value.isDirectory }
			// Case: Targeting directories => May apply 'fileFilter' to directories
			if (targetDirectories) {
				if (filterBasedOnContent)
					base.filter { dir => dir.children.exists { p => p.value.isRegularFile && fileFilter(p.value) } }
						.map { _.value -> Empty }
				else
					base.map { _.value }.filter(fileFilter).map { _ -> Empty }
			}
			// Case: Targeting files
			//       => Applies 'fileFilter' to files and includes only directories with 1+ targetable files
			else
				base
					.map { p =>
						p.value -> p.children.iterator.map { _.value }.filter { p => p.isRegularFile && fileFilter(p) }
							.toOptimizedSeq
					}
					.filter { _._2.nonEmpty }
		}
		arg.string match {
			// Case: Custom target => Checks whether it's an existing file or directory
			case Some(target) =>
				Some(target: Path).filter { _.exists } match {
					// Case: Exists => Checks whether it's a regular file
					case Some(targetPath) =>
						// Case: Directory (when not targeted) => Asks the user to select a file from it
						if (targetPath.isDirectory && !targetDirectories)
							selectFromDirectoryFiles(targetPath,
								targetPath.iterateChildren {
									_.filter { p => p.isRegularFile && fileFilter(p) }.toOptimizedSeq }
									.logWith("File system reading failed",
										subContext = "resolve.path.fromInput.directory.listFiles")
									.getOrElse(Empty))
						// Case: Regular file or targeted directory => Selects it
						else
							Some(targetPath)
						
					// Case: (Likely) not a file reference
					case None =>
						// Case: Looks like an absolute path => Treats it as a non-existing file and fails
						if (target.headOption.contains('/')) {
							println(s"`$target` doesn't exist")
							None
						}
						// Case: Other input string => Looks whether a file or a directory with that name can be found
						else {
							val directoriesIter = allDirectoriesIter.flatMap { case (dir, files) =>
								val hasMatchingName = dir.fileName.containsIgnoreCase(target)
								// Case: Targets directories => Directory name must match the input
								if (targetDirectories) {
									if (hasMatchingName)
										Some(dir -> files)
									else
										None
								}
								// Case: Directory name contains specified input & targeting regular files
								//       => Accepts all files from that directory
								else if (hasMatchingName)
									Some(dir -> files)
								// Case: Directory name is not a match & targeting regular files
								//       => Only targets matching files
								else {
									val matchingFiles = files.filter { _.fileName.containsIgnoreCase(target) }
									// Case: No targetable files => Skips this directory
									if (matchingFiles.isEmpty)
										None
									else
										Some(dir -> matchingFiles)
								}
							}
							// Allows the user to select from the results (or to specify another file)
							selectFromDirectories(directoriesIter, prompt, targetDirectories)
						}
				}
			// Case: No custom target => Scans the local directories for possible options
			case None =>
				// Allows the user to select from the results (or to specify another file)
				selectFromDirectories(allDirectoriesIter, prompt, targetDirectories)
		}
	}
	
	private def selectFromDirectories(directoriesIter: Iterator[(Path, Seq[Path])], prompt: String,
	                                  targetDirectories: Boolean)
	                                 (implicit scribe: Scribe): Option[Path] =
	{
		// Finds the next 5 directories to display
		val nextOptions = directoriesIter.collectNext(5)
		// Case: No results => Asks the user to specify a path
		if (nextOptions.isEmpty)
			askForPath(prompt)
		// Case: Directories found => Asks the user to select one of them
		else {
			println(prompt)
			val baseOptions = nextOptions.map { case (dir, files) =>
				val target = {
					if (targetDirectories)
						dir -> files
					else
						files.only match {
							case Some(file) => file -> Empty
							case None => dir -> files
						}
				}
				Some(target) -> target._1.toAbsolutePath.toJson
			}
			// May also include an option "Look further" for another search iteration
			val options = if (directoriesIter.hasNext) baseOptions :+ (None -> "Look further") else baseOptions
			StdIn
				.selectFromOrAdd(options, "directories") {
					Some(askForPath(s"Please specify the targeted ${ if (targetDirectories) "directory" else "file" }")
						.map { selected =>
							if (targetDirectories || selected.isRegularFile)
								selected -> Empty
							else
								selected -> selected.iterateChildren { _.filter { _.isRegularFile }.toOptimizedSeq }
									.logWith("File system reading failed",
										subContext = "resolve.path.selected.directory.listFiles")
									.getOrElse(Empty)
						})
				}
				.flatMap {
					// Case: Selected a directory or a file
					case Some((selectedPath, files)) =>
						// Case: Regular file found => Search completes
						if (targetDirectories || selectedPath.isRegularFile)
							Some(selectedPath)
						// Case: Selected a directory => Accepts it or asks the user to select a file from it
						else
							selectFromDirectoryFiles(selectedPath, files)
					
					// Case: Selected "Look further" => Uses recursion
					case None => selectFromDirectories(directoriesIter, prompt, targetDirectories)
				}
		}
	}
	
	private def selectFromDirectoryFiles(directory: Path, files: Seq[Path]) =
		files.emptyOneOrMany match {
			// Case: No files found => Fails
			case None =>
				println(s"No files were found under ${ directory.toAbsolutePath }")
				None
			// Case: Only one file found => Selects it
			case Some(Left(only)) => Some(only)
			// Case: Multiple files found => Asks the user to select one
			case Some(Right(options)) =>
				println("Select from the following options:")
				StdIn.selectFrom(options.map { p => p -> p.fileName }, "files")
		}
		
	private def askForPath(prompt: String) = StdIn.readNonEmptyLine(prompt) match {
		case Some(input) =>
			val path = input: Path
			if (path.exists)
				Some(path)
			else {
				println(s"`$input` doesn't exist")
				None
			}
		case None =>
			println("Canceled by user")
			None
	}
}
