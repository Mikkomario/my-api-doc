package vf.myapidoc.util

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.async.context.{Scheduler, ThreadPool}
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.logging.SysErrLogger
import utopia.scribe.core.controller.logging.{ConsoleScribe, LazyScribe, Synagogue}
import utopia.vault.database.ConnectionPool

import java.nio.file.Path
import scala.concurrent.ExecutionContext
import scala.io.Codec

/**
 * Contains commonly used values
 * @author Mikko Hilpinen
 * @since 15.10.2026, v0.1
 */
object Common
{
	// ATTRIBUTES	--------------------
	
	implicit val codec: Codec = Codec.UTF8
	
	private val synagogue = new Synagogue()
	
	implicit val jsonParser: JsonBunny.type = JsonBunny
	implicit val exc: ExecutionContext = new ThreadPool("MyApiDoc")(SysErrLogger)
	implicit val scheduler: Scheduler = SysErrLogger.use { implicit log => Scheduler.newInstance }
	
	implicit val cPool: ConnectionPool = new ConnectionPool()
	val dbName = "my_api_doc_db"
	
	/**
	 * Directory that contains the application data
	 */
	val dataDirectory: Path = "data"
	/**
	 * Directory that contains generated examples
	 */
	val examplesDirectory = dataDirectory / "examples"
	
	
	// INITAL CODE  --------------------
	
	// Sets up file logging
	synagogue.register(ConsoleScribe.copyingToFile("log", "MyApiDoc", bundleDuration = 1.seconds), priority = true)
	
	
	// OTHER    ------------------------
	
	/**
	 * Creates a new Scribe logging interface
	 * @param context Context in which logging will be performed
	 * @return A lazily initialized scribe interface
	 */
	def newScribe(context: => String) = LazyScribe { synagogue.in(context) }
}
