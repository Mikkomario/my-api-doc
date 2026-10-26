package vf.myapidoc.controller

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.file.FileUtils
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.util.logging.ScribeTryExtensions._

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
	 * Resolves a path together with the user
	 * @param arg Received file (command) argument
	 * @param prompt Prompt to display before asking the user to select a file or a directory
	 * @param fileFilter Filter that only accepts valid files
	 * @param scribe Implicit scribe interface for logging
	 * @return Selected (existing) regular file path. None if not found or canceled by the user.
	 */
	def resolvePath(arg: Value = Value.empty, prompt: String = "Please specify the targeted file")
	               (fileFilter: Path => Boolean)(implicit scribe: Scribe) =
		arg.string match {
			// Case: Custom target => Checks whether it's an existing file or directory
			case Some(target) =>
				Some(target: Path).filter { _.exists } match {
					// Case: Exists => Checks whether it's a regular file
					case Some(targetPath) =>
						// Case: Directory => Asks the user to select a file from it
						if (targetPath.isDirectory)
							selectFromDirectoryFiles(targetPath)(fileFilter)
						// Case: Regular file => Selects it
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
							val directoriesIter = FileUtils.workingDirectory.toTree.topDownNodesIterator
								.map { _.value }
								.filter { p =>
									p.isDirectory && (p.fileName.containsIgnoreCase(target) ||
										p.iterateChildren { _.exists { _.fileName.containsIgnoreCase(target) } }
											.getOrElse(false))
								}
							// Allows the user to select from the results (or to specify another file)
							selectFromDirectories(directoriesIter, prompt) { p =>
								fileFilter(p) && p.fileName.containsIgnoreCase(target)
							}
						}
				}
			// Case: No custom target => Scans the local directories for possible options
			case None =>
				val directoriesIter = FileUtils.workingDirectory.toTree.topDownNodesIterator.filter { p =>
					p.value.isDirectory && p.children.exists { p => p.value.isRegularFile && fileFilter(p.value) }
				}
				// Allows the user to select from the results (or to specify another file)
				selectFromDirectories(directoriesIter.map { _.value }, prompt)(fileFilter)
		}
		
	private def selectFromDirectories(directoriesIter: Iterator[Path], prompt: String)(fileFilter: Path => Boolean)
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
			val baseOptions = nextOptions.map { p => Some(p) -> p.toAbsolutePath.toJson }
			// May also include an option "Look further" for another search iteration
			val options = if (directoriesIter.hasNext) baseOptions :+ (None -> "Look further") else baseOptions
			StdIn
				.selectFromOrAdd(options, "directories") { Some(askForPath("Please specify the targeted file")) }
				.flatMap {
					// Case: Selected a directory or a file
					case Some(selectedPath) =>
						// Case: Regular file found (not normal use-case) => Search completes
						if (selectedPath.isRegularFile)
							Some(selectedPath)
						// Case: Selected a directory => Asks the user to select a file from it
						else
							selectFromDirectoryFiles(selectedPath)(fileFilter)
					
					// Case: Selected "Look further" => Uses recursion
					case None => selectFromDirectories(directoriesIter, prompt)(fileFilter)
				}
		}
	}
	
	private def selectFromDirectoryFiles(directory: Path)(fileFilter: Path => Boolean)(implicit scribe: Scribe) =
		directory
			// Finds the best file options
			.iterateChildren { _.filter { _.isRegularFile }.toOptimizedSeq.bestMatch(fileFilter) }
			// Handles file system read errors
			.logWith("File system reading failed", subContext = "resolve.path.selected.directory.listFiles")
			.getOrElse(Empty)
			.emptyOneOrMany match
		{
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
