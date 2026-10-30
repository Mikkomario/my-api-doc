package vf.myapidoc.controller.testing

import utopia.access.model.Headers
import utopia.access.model.enumeration.Method.{Delete, Get}
import utopia.access.model.enumeration.{Method, Status}
import utopia.disciple.controller.Gateway
import utopia.disciple.controller.parse.ResponseParser
import utopia.disciple.model.request.{Request, StringBody, Timeout}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Pair}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.generic.model.mutable.MutableModel
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.file.KeptOpenWriter
import utopia.flow.parse.string.Regex
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{Now, Today}
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.Mutate
import utopia.flow.util.StringExtensions._
import utopia.flow.util.result.TryExtensions._
import utopia.flow.view.mutable.Pointer
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.model.enumeration.Severity
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import vf.myapidoc.controller.testing.TestContext.{TestScribe, pathParamR}
import vf.myapidoc.model.cached.testing.instruction.TestInstruction
import vf.myapidoc.model.combined.api.DetailedApiNode
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.error.TestFailedException
import vf.myapidoc.util.Common._

import java.nio.file.Paths
import scala.util.{Failure, Success}

object TestContext
{
	// ATTRIBUTES   --------------------------
	
	private val pathParamR = {
		val closing = Regex.escape('}')
		Regex.escape('{') + (!closing).withinParentheses.anyTimes + closing
	}
	
	
	// NESTED   ------------------------------
	
	private class TestScribe(wrapped: Scribe, eventLog: KeptOpenWriter) extends Scribe
	{
		// IMPLEMENTED  ----------------------
		
		override def self: Scribe = this
		
		override def in(subContext: String): Scribe = map { _.in(subContext) }
		override def apply(severity: Severity): Scribe = map { _(severity) }
		override def variant(details: Model): Scribe = map { _.variant(details) }
		
		override def apply(error: Option[Throwable], message: String, details: Model): Unit = {
			wrapped(error, message, details)
			eventLog { writer =>
				error.foreach { error => writer.println(s"FAILURE: ${ error.getMessage }") }
				if (message.nonEmpty)
					writer.println(message)
				if (details.nonEmpty) {
					writer.println("Details:")
					details.propertiesIterator.foreach { detail =>
						writer.println(s"- ${ detail.name }: ${ detail.value }")
					}
				}
			}
		}
		
		
		// OTHER    ---------------------------
		
		private def map(f: Mutate[Scribe]) = new TestScribe(f(wrapped), eventLog)
	}
}

/**
 * A stateful context for running tests
 * @param apiRootUrl URL to the API root. Should NOT end with '/'.
 * @param tests Named tests that may be run using [[run]]
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class TestContext(apiRootUrl: String, tests: Seq[(String, TestInstruction)] = Empty)
{
	// ATTRIBUTES   --------------------------
	
	private val eventLog = newScribe("testing.Context.writer").use { implicit scribe =>
		KeptOpenWriter(30.seconds).logging.to(Paths.get(s"log/test-runs/$Today-run-1.txt").unique)
	}
	/**
	 * Logging interface to use during testing
	 */
	implicit val scribe: Scribe = Scribe.lazily { new TestScribe(newScribe("testing.Context"), eventLog) }
	
	private val gateway = Gateway(maxConnectionsPerRoute = 1, maximumTimeout = Timeout(30.seconds, 90.seconds),
		connectionTimeout = 10.seconds)
	
	private val state = MutableModel()
	private var sensitiveKeys = Set[String]()
	
	/**
	 * Contains the currently used authorization method, which is either:
	 *      - Right: A bearer token
	 *      - Left: Basic auth user + password
	 */
	private val authP = Pointer.eventful[Either[Pair[String], String]](Right(""))
	private val headersP = authP.map {
		case Left(auth) => Headers.withBasicAuthorization(auth.first, auth.second)
		case Right(token) =>
			if (token.isEmpty)
				Headers.empty
			else
				Headers.withBearerAuthorization(token)
	}
	
	private var _lastResponseStatus: Option[Status] = None
	private var _lastResponseBody = Value.empty
	
	
	// COMPUTED ---------------------------
	
	/**
	 * @return Status of the last received response. None if the last request failed or if no requests have been made.
	 */
	def lastResponseStatus = _lastResponseStatus
	/**
	 * @return Body within the last received response. If no response was received, yields an empty value.
	 */
	def lastResponseBody = _lastResponseBody
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param key A key
	 * @return Current value assigned to the specified key
	 */
	def apply(key: String) = key.split(Regex.dot).oneOrMany match {
		case Left(key) => state(key)
		case Right(path) => nested(path)
	}
	/**
	 * @param path A path to the targeted property
	 * @return Currently assigned value in the targeted property
	 */
	def nested(path: IterableOnce[String]) = state.nested(path)
	/**
	 * Updates a context value
	 * @param key Targeted key
	 * @param value New value to assign
	 */
	def update(key: String, value: Value): Unit = key.split(Regex.dot).oneOrMany match {
		case Left(key) => state(key) = value
		case Right(path) => update(path, value)
	}
	/**
	 * Updates a possibly nested context value
	 * @param path Path to the targeted value. E.g. ["body", "property"]
	 * @param value Value to assign to the end of that path
	 * @throws IllegalArgumentException If 'path' is empty
	*/
	@throws[IllegalArgumentException]("If targeting an empty path")
	def update(path: IterableOnce[String], value: Value): Unit = {
		val pathIter = path.iterator
		if (pathIter.hasNext) {
			val primaryKey = pathIter.next()
			// Case: Targeting a nested value => Modifies or creates a model value
			if (pathIter.hasNext)
				state(primaryKey) = state(primaryKey).getModel.withNestedValue(pathIter, value)
			// Case: Targeting a direct value => Assigns the new value to this context directly
			else
				update(primaryKey, value)
		}
		else
			throw new IllegalArgumentException("Path must contain at least one element")
	}
	
	/**
	 * @param key A key
	 * @return Whether the specified key should be considered sensitive
	 */
	def isSensitive(key: String) = sensitiveKeys.contains(key.toLowerCase)
	/**
	 * Marks a key as sensitive. Sensitive values are not logged.
	 * @param key Key that should be considered sensitive.
	 */
	def markSensitive(key: String): Unit = sensitiveKeys += key.toLowerCase
	
	/**
	 * Updates the authorization method used in future requests to use basic authorization
	 * @param user User
	 * @param password Password
	 */
	def setBasicAuth(user: String, password: String) = authP.value = Left(user -> password)
	/**
	 * Updates the authorization method used in future requests
	 * @param auth Authorization method to use (a bearer token or a user-password combo separated using '+')
	 */
	def setAuth(auth: String) = authP.value = {
		if (auth.contains('+'))
			Left(auth.splitAtFirst("+").map { _.trim })
		else
			Right(auth.trim)
	}
	
	/**
	 * Performs a HTTP request
	 * @param method Method to use
	 * @param pathOrNode Targeted relative path or API node
	 */
	def request(method: Method, pathOrNode: Either[String, DetailedApiNode]) = {
		// Resolves the path parameters. Fails if all parameters can't be resolved.
		val path = resolvePath(pathOrNode)
		// May include a request body from the current state
		val body: Value = {
			// Case: Body not supported by the used method => Skips body
			if (method == Get || method == Delete)
				Value.empty
			else
				resolveBody(pathOrNode, method)
		}
		log(s"Sending $method $path${ if (body.isEmpty) "" else " including a body" }")
		val startTime = Now.toInstant
		gateway
			.makeBlockingRequest(
				Request(s"$apiRootUrl${ path.startingWith("/") }", method,
					body = if (body.isEmpty) None else Some(StringBody.json(body.toJson)), headers = headersP.value)) {
				response =>
					_lastResponseStatus = Some(response.status)
					response.consume(ResponseParser.string).wrapped match {
						case Success(bodyStr) =>
							_lastResponseBody = jsonParser.valueOf(bodyStr)
							log(s"Received ${ response.status.code }: $bodyStr\nRequest took ${
								(Now - startTime).description }")
							
							// Documents the response body, if received for a documented API node
							// TODO: Once we have the models, document a DB entry of this example
							pathOrNode.foreach { node =>
								val standardizedName = node.name.toLowerCase.replace(" ", "-")
								Paths
									.get(s"examples/$standardizedName/$method/${
										response.status.code }/$Today-$method-$standardizedName-${
										response.status.code }-1.json")
									.createParentDirectories()
									.flatMap { path => path.unique.write(bodyStr) }
									.logWith("Failed to write a response body example")
							}
						
						case Failure(error) =>
							scribe(error, "Failed to parse a response body",
								Model.from("method" -> method.name, "path" -> path))
							_lastResponseBody = Value.empty
					}
			}
			.failure
			.foreach { error =>
				_lastResponseStatus = None
				_lastResponseBody = Value.empty
				scribe(error, "Failed to send a request", Model.from("method" -> method.name, "path" -> path))
			}
	}
	
	/**
	 * Runs a named test
	 * @param test Name of the test to run
	 */
	def run(test: String): Unit = tests.find { _._1 ~== test } match {
		case Some((testName, testCode)) =>
			log(s"\nrun($testName):")
			testCode.execute()(this)
			log(s"$testName completed\n")
			
		case None =>
			val message = s"Attempted to run a non-existing test: $test"
			log(s"Failure: $message")
			throw new TestFailedException(message)
	}
	
	/**
	 * Writes a new test log entry
	 * @param line Line to add to the test event log
	 */
	def log(line: String): Unit = eventLog { _.println(line) }
	
	private def resolvePath(pathOrNode: Either[String, DetailedApiNode]) = pathOrNode match {
		// Case: Targeting a custom path => Resolves path parameters using a regular expression
		case Left(relativePath) =>
			pathParamR.divide(relativePath).iterator
				.map { _.leftOrMap { param =>
					apply(param.drop(1).dropRight(1)).tryString match {
						case Success(value) => value
						case Failure(error) =>
							val message = s"Couldn't resolve $param in $relativePath"
							log(s"Failure: $message")
							throw new TestFailedException(message, error)
					}
				} }
				.mkString
		// Case: Targeting an API node => Resolves the documented path parameters
		case Right(node) =>
			node.pathParams.foldLeft(node.absolutePath) { (path, param) =>
				apply(param.name).tryString match {
					case Success(value) => path.replace(s"{${ param.name }", value)
					case Failure(error) =>
						val message = s"Couldn't resolve ${ node.name }'s path parameter $param"
						log(s"Failure: $message")
						throw new TestFailedException(message, error)
				}
			}
	}
	private def resolveBody(pathOrNode: Either[String, DetailedApiNode], method: Method): Value = {
		val customBody = apply("body")
		pathOrNode.toOption match {
			// Case: Targeting a documented API node
			//       => Uses the custom body or constructs a body using contextual information
			case Some(node) =>
				node.get(method) match {
					case Some(impl) =>
						// Uses the first defined request body schema
						impl.requestBodyOptions.headOption match {
							// Case: Request body documentation found
							case Some(bodySchema) =>
								val body = customBody.nonEmptyOrElse {
									// Attempts to construct the body using contextual information
									bodySchema.modelType.flatMap(constructModel): Value
								}
								// Case: Required body is missing => Fails
								if (impl.requiresRequestBody && body.isEmpty) {
									val message = "Required body not specified"
									log(s"Failure: $message")
									throw new TestFailedException(message)
								}
								// Case: An array is requested => Attempts to convert the body into a vector
								else if (bodySchema.isArray)
									body.getVector
								else
									body
								
							// Case: No request body documentation is present
							//       => May fail if a required body is missing
							case None =>
								if (impl.requiresRequestBody && customBody.isEmpty) {
									val message = s"$method ${ node.name }'s request body hasn't been documented"
									log(s"Failure: $message")
									throw new TestFailedException(message)
								}
								else
									customBody
						}
					// Case: Undocumented method implementation
					//       => Logs a warning and applies the custom body, if specified
					case None =>
						log(s"Warning: No documentation exists for $method ${ node.name }")
						customBody
				}
			// Case: Custom request => Applies the custom body, if specified
			case None => customBody
		}
	}
	
	private def constructModel(modelSchema: DetailedModelSchema): Option[Model] = {
		val result = Model(modelSchema.properties.flatMap[(String, Value)] { prop =>
			val value: Value = prop.contentType match {
				case Some(contentSchema) => constructModel(contentSchema)
				case None => apply(prop.name)
			}
			if (value.isEmpty)
				None
			else
				Some(prop.name -> (if (prop.isArray.isCertainlyTrue) value.getVector else value))
		})
		result.notEmpty
	}
}
