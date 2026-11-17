package vf.myapidoc.controller.testing

import utopia.access.model.Headers
import utopia.access.model.enumeration.Method.{Delete, Get}
import utopia.access.model.enumeration.{Method, Status}
import utopia.disciple.controller.Gateway
import utopia.disciple.controller.parse.ResponseParser
import utopia.disciple.model.request.{Request, StringBody, Timeout}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Pair, Single}
import utopia.flow.error.ErrorExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.generic.model.mutable.DataType.{ModelType, PairType, VectorType}
import utopia.flow.generic.model.mutable.MutableModel
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.parse.BufferedPrintWriter
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.file.KeptOpenWriter
import utopia.flow.parse.string.{Regex, StringFrom}
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{Now, Today}
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.result.TryExtensions._
import utopia.flow.util.{AppConfig, Mutate, NotEmpty}
import utopia.flow.view.immutable.View
import utopia.flow.view.mutable.Pointer
import utopia.scribe.core.controller.logging.Scribe
import utopia.scribe.core.model.enumeration.Severity
import utopia.scribe.core.util.logging.ScribeTryExtensions._
import vf.myapidoc.controller.testing.TestContext.{TestScribe, pathParamR}
import vf.myapidoc.database.store.ExamplesDb
import vf.myapidoc.database.value.DocUpdateId
import vf.myapidoc.model.cached.testing.Test
import vf.myapidoc.model.combined.api.{DetailedApiNode, DetailedPossibleResponse}
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.error.TestFailedException
import vf.myapidoc.util.Common._

import java.nio.file.Paths
import scala.util.{Failure, Success, Try}

object TestContext
{
	// ATTRIBUTES   --------------------------
	
	private val pathParamR = {
		val closing = Regex.escape('}')
		Regex.escape('{') + (!closing).withinParentheses.anyTimes + closing
	}
	
	
	// NESTED   ------------------------------
	
	private class TestScribe(wrapped: Scribe, eventLogView: View[KeptOpenWriter]) extends Scribe
	{
		// IMPLEMENTED  ----------------------
		
		override def self: Scribe = this
		
		override def in(subContext: String): Scribe = map { _.in(subContext) }
		override def apply(severity: Severity): Scribe = map { _(severity) }
		override def variant(details: Model): Scribe = map { _.variant(details) }
		
		override def apply(error: Option[Throwable], message: String, details: Model): Unit = {
			wrapped(error, message, details)
			eventLogView.value { writer =>
				writer.println()
				error.foreach { error =>
					writer.println(s"FAILURE: ${ error.getMessage }")
					error.causesIterator.foreach { cause => println(s"\t${ cause.getMessage }") }
				}
				if (message.nonEmpty)
					writer.println(message)
				if (details.nonEmpty) {
					writer.println("Details:")
					details.propertiesIterator.foreach { detail =>
						writer.println(s"- ${ detail.name }: ${ detail.value }")
					}
				}
				writer.println()
			}
		}
		
		
		// OTHER    ---------------------------
		
		private def map(f: Mutate[Scribe]) = new TestScribe(f(wrapped), eventLogView)
	}
}

/**
 * A stateful context for running tests
 * @param apiId ID of the tested API
 * @param apiName Name of the API that is being tested
 * @param apiRootUrl URL to the API root. Should NOT end with '/'.
 * @param testsView A view into named tests callable in this context
 * @param sensitiveApiPropertyNames Names of properties documented as sensitive in the API.
 * @author Mikko Hilpinen
 * @since 29.10.2026, v0.1
 */
class TestContext(apiId: Int, apiName: String, apiRootUrl: String, testsView: View[Iterable[Test]] = View.fixed(Empty),
                  sensitiveApiPropertyNames: Set[String] = Set[String]())
{
	// ATTRIBUTES   --------------------------
	
	private val altScribe = newScribe("testing.Context").variant("api", apiName)
	private val lastTestP = Pointer.eventful("")(altScribe)
	private val eventLogP = altScribe.in("writer").use { implicit scribe =>
		lastTestP.map { test =>
			KeptOpenWriter(30.seconds).logging.to(Paths.get(
				s"log/test-runs/$Today${ test.toLowerCase.replace(' ', '-').prependIfNotEmpty("-") }-run-1.txt").unique)
		}
	}
	/**
	 * Logging interface to use during testing
	 */
	implicit val scribe: Scribe = Scribe.lazily { new TestScribe(newScribe("testing.Context"), eventLogP) }
	
	private val testsMapView = testsView.mapValue { _.iterator.map { t => standardizeRef(t.name) -> t }.toMap }
	
	private val gateway = Gateway(maxConnectionsPerRoute = 1, maximumTimeout = Timeout(30.seconds, 90.seconds),
		connectionTimeout = 10.seconds)
	private val config = AppConfig("myapidoc",
		fileName = s"${ apiName.replace(' ', '-').toLowerCase }-test-context.json",
		allowWorkingDirectoryAsAppDirectory = true)
		.logWith("Failed to initialize the persisted test config interface", subContext = "init.config")
	
	private val state = MutableModel()
	/**
	 * Contains the names of properties persisted between sessions.
	 * All keys are lower case.
	 */
	private var _persistedKeys = Set[String]()
	/**
	 * Contains names of properties that should be considered sensitive and not printed anywhere.
	 * All keys are lower case.
	 */
	private var sensitiveKeys = sensitiveApiPropertyNames.map { _.toLowerCase }
	
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
	
	private var preferredPathElements: Seq[String] = Empty
	
	
	// INITIAL CODE -----------------------
	
	// Sets up persistence using AppConfig
	config.foreach { config =>
		// Loads the previously persisted state
		currentState = config
		
		config.nonEmptyPropertiesIterator.notEmpty.foreach { persistedPropsIter =>
			println(s"Loaded previously persisted variables: [${ persistedPropsIter.map { _.name }.mkString(", ") }]")
		}
		
		// Loads persisted sensitive keys
		val sensitiveKeysInConfig = config("sensitiveKeys").getVector.iterator.flatMap { _.string }.toSet
		if (sensitiveKeysInConfig.isEmpty) {
			if (sensitiveKeys.nonEmpty)
				config("sensitiveKeys") = sensitiveKeys.toVector.sorted
		}
		else {
			sensitiveKeys ++= sensitiveKeysInConfig
			if (sensitiveKeys != sensitiveKeysInConfig)
				config("sensitiveKeys") = sensitiveKeys.toVector.sorted
		}
		
		// Whenever a persisted property value changes, stores it in the AppConfig
		state.addListener { e =>
			val lowerPropName = e.property.name.toLowerCase
			if (_persistedKeys.contains(lowerPropName)) {
				config(e.property.name) = e.newValue
				
				// TODO: Possibly remove this debug log entry
				val valueStr = {
					if (sensitiveKeys.contains(lowerPropName))
						"<omitted>"
					else
						e.newValue.toJson
				}
				log(s"Persisted ${ e.property.name } = $valueStr")
			}
		}
	}
	
	
	// COMPUTED ---------------------------
	
	/**
	 * @return Status of the last received response. None if the last request failed or if no requests have been made.
	 */
	def lastResponseStatus = _lastResponseStatus
	/**
	 * @return Body within the last received response. If no response was received, yields an empty value.
	 */
	def lastResponseBody = _lastResponseBody
	
	/**
	 * @return The current context state, including all context variables
	 */
	def currentState = state.immutableCopy
	def currentState_=(state: HasProperties) = state.nonEmptyPropertiesIterator
		.foreach { prop => update(prop.name, prop.value) }
	
	/**
	 * @return Names of currently persisted properties.
	 *         Persisted properties are loaded at the start of the next session.
	 */
	def persistedKeys = config match {
		case Some(config) => config.nonEmptyPropertiesIterator.map { _.name }.toOptimizedSeq
		case None => Empty
	}
	
	
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
	 * Prints a value to the console and to the log.
	 * Omits sensitive property values.
	 * @param value Value to print
	 * @param skipLog Whether this value should NOT be printed to the log
	 */
	def printValue(value: Value, skipLog: Boolean = false) = {
		// Buffers the value representation into a string
		StringFrom.writer { writer => logBodyValue(writer, value, sensitiveKeys, 0) } match {
			case Success(valueStr) =>
				// Prints the value to both stdout and the log
				println(valueStr)
				if (!skipLog)
					log(valueStr)
				
			// Case: Failed to write the value into a string => Logs
			case Failure(error) =>
				scribe.in("print.value")(error, "Failed to write a value into a string")
		}
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
	def markSensitive(key: String): Unit = {
		val lowerKey = key.toLowerCase
		if (!sensitiveKeys.contains(lowerKey)) {
			sensitiveKeys += lowerKey
			config.foreach { _("sensitiveKeys") = sensitiveKeys.toVector.sorted }
		}
	}
	
	/**
	 * Marks a property as persisting, so that it will be loaded into consecutive sessions.
	 * @param propertyName Name of the persisted property.
	 */
	def persist(propertyName: String) = {
		val lowerKey = propertyName.toLowerCase
		if (!_persistedKeys.contains(lowerKey)) {
			_persistedKeys += lowerKey
			// Persists the current value
			config.foreach { _(propertyName) = state(propertyName) }
		}
	}
	
	/**
	 * Marks a request path element as preferred.
	 * This affects which request path is selected in situations where multiple are available.
	 * @param pathElement Path element to prefer
	 */
	def preferPathElement(pathElement: String) = {
		preferredPathElements.headOption match {
			case Some(preferred) =>
				if (preferred != pathElement)
					preferredPathElements = pathElement +: preferredPathElements.filterNot { _ == pathElement }
			
			case None => preferredPathElements = Single(pathElement)
		}
	}
	
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
	def request(method: Method, pathOrNode: Either[String, DetailedApiNode], line: Int) = {
		// Resolves the path parameters. Fails if all parameters can't be resolved.
		val path = resolvePath(pathOrNode, line)
		// May include a request body from the current state
		val body: Value = {
			// Case: Body not supported by the used method => Skips body
			if (method == Get || method == Delete)
				Value.empty
			else
				resolveRequestBody(pathOrNode, method, line)
		}
		log(s"\nSending $method $path")
		logBody(body, sensitiveKeys)
		val startTime = Now.toInstant
		// Executes the request
		gateway
			.makeBlockingRequest(
				Request(s"$apiRootUrl${ path.startingWith("/") }", method,
					body = if (body.isEmpty) None else Some(StringBody.json(body.toJson)), headers = headersP.value)) {
				response =>
					// Handles and records the received response
					_lastResponseStatus = Some(response.status)
					log(s"Received ${ response.status.code }\nRequest took ${ (Now - startTime).description }")
					response.consume(ResponseParser.string).wrapped match {
						case Success(bodyStr) =>
							if (bodyStr.isEmpty)
								_lastResponseBody = Value.empty
							else {
								val bodyValue = jsonParser.valueOf(bodyStr)
								_lastResponseBody = bodyValue
								
								// Checks whether the expected response has been documented
								// Logs if not
								val statusDoc = pathOrNode.toOption.flatMap { node =>
									node.get(method) match {
										case Some(impl) =>
											val result = impl.responseWithStatus(response.status)
											if (result.isEmpty)
												altScribe.in("undocumented.response.status").info
													.variant(Model.from("apiId" -> apiId, "node" -> node.name,
														"method" -> method.name, "status" -> response.status.code))
													.apply("Received a response status not found from the documentation")
											result
										case None =>
											altScribe.in("undocumented.response").info
												.variant(Model.from("apiId" -> apiId, "node" -> node.name,
													"method" -> method.name))
												.apply("Received a response for an undocumented method implementation")
											None
									}
								}
								
								logBody(bodyValue, sensitivePropNames = statusDoc.iterator
									.flatMap { _.sensitivePropertyNamesIterator.map { _.toLowerCase } })
								pathOrNode.toOption.foreach { node =>
									recordResponse(node.name, method, response.status.code, statusDoc, bodyStr,
										bodyValue)
								}
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
	 * @param testName Name of the targeted test
	 * @return Test that matches that name. None if not found.
	 */
	def getTest(testName: String) = testsMapView.value.get(standardizeRef(testName))
	/**
	 * Marks a test as started. Affects logging.
	 * @param test The test that's about to start.
	 */
	def markStarted(test: String) = lastTestP.value = test
	
	/**
	 * Fully clears / resets this test context, including all persisted values.
	 */
	def clear() = {
		lastTestP.value = ""
		config.foreach { _.clear() }
		state.propertiesIterator.foreach { _.value = Value.empty }
		_persistedKeys = Set()
		sensitiveKeys = Set()
		authP.value = Right("")
		_lastResponseStatus = None
		_lastResponseBody = Value.empty
		preferredPathElements = Empty
	}
	
	/**
	 * Writes a new test log entry
	 * @param line Line to add to the test event log
	 */
	def log(line: String): Unit = useLog { _.println(line) }
	def useLog[A](f: BufferedPrintWriter => A) = eventLogP.value(f)
	
	private def resolvePath(pathOrNode: Either[String, DetailedApiNode], line: Int) = pathOrNode match {
		// Case: Targeting a custom path => Resolves path parameters using a regular expression
		case Left(relativePath) =>
			pathParamR.divide(relativePath).iterator
				.map { _.leftOrMap { param =>
					apply(param.drop(1).dropRight(1)).tryString match {
						case Success(value) => value
						case Failure(error) =>
							val message = s"Couldn't resolve $param in $relativePath"
							log(s"Failure: $message")
							throw new TestFailedException(message, line, cause = error)
					}
				} }
				.mkString
		// Case: Targeting an API node => Resolves the documented path parameters
		case Right(node) =>
			// Checks whether multiple alternative paths are available.
			// Orders the paths, if so.
			val pathOptions = node.absolutePaths.oneOrMany match {
				case Left(only) => Single(only)
				case Right(options) =>
					val prefCount = preferredPathElements.size
					options.sortBy { case (path, _) =>
						val pathElems = path.splitIterator(Regex.forwardSlash).toSet
						preferredPathElements.findIndexWhere(pathElems.contains).getOrElse(prefCount)
					}
			}
			// Attempts to resolve the path parameters.
			pathOptions.tryMapAny { case (path, params) =>
				params.foldLeft[Try[String]](Success(path)) { (path, param) =>
					path.flatMap { path =>
						apply(param.name).tryString match {
							case Success(value) => Success(path.replace(s"{${ param.name }", value))
							case Failure(error) =>
								Failure(new TestFailedException(s"Couldn't resolve path parameter: ${ param.name }",
									line, cause = error))
						}
					}
				}
			} match {
				// Case: Path acquired
				case Right(path) =>
					// Logs if multiple paths were available
					if (pathOptions.hasSize > 1)
						log(s"Uses path $path")
					path
				
				// Case: Failed to resolve the path => Throws
				case Left(failures) =>
					failures.emptyOneOrMany match {
						case None =>
							throw new TestFailedException(s"${ node.name } doesn't specify a request path", line)
						case Some(Left(failure)) =>
							throw new TestFailedException(s"Couldn't resolve ${ node.name }'s path parameters", line,
								cause = failure)
						case Some(Right(failures)) =>
							failures.logWith("Failed to resolve path parameters", Model.from("node" -> node.name),
								subContext = "resolve.path.multipleOptions.failed")
							throw new TestFailedException(s"Failed to resolve ${ node.name }'s path parameters", line,
								cause = failures.head)
					}
			}
	}
	private def resolveRequestBody(pathOrNode: Either[String, DetailedApiNode], method: Method, line: Int): Value = {
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
									throw new TestFailedException(message, line)
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
									throw new TestFailedException(message, line)
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
			if (value.isEmpty) {
				if (prop.isRequired)
					scribe.in("construct.model.missingValue").warning("Required property value is missing",
						Model.from("schemaId" -> modelSchema.id, "schemaName" -> modelSchema.name,
							"propName" -> prop.name))
				None
			}
			else
				Some(prop.name -> (if (prop.isArray.isCertainlyTrue) value.getVector else value))
		})
		result.notEmpty
	}
	
	// Expects sensitivePropNames to be in lower case
	private def logBody(body: Value, sensitivePropNames: => IterableOnce[String]) = {
		if (body.nonEmpty)
			body.model match {
				case Some(body) =>
					if (body.nonEmpty)
						useLog { writer => logBodyValue(writer, body, Set.from(sensitivePropNames), 0) }
				case None =>
					NotEmpty(body.getVector).foreach { bodyElements =>
						useLog { writer =>
							val sensitive = Set.from(sensitivePropNames)
							bodyElements.iterator.zipWithIndex.foreach { case (elem, index) =>
								writer.print(s"${ index + 1 }. ")
								logBodyValue(writer, elem, sensitive, 0)
							}
						}
					}
			}
	}
	private def logBodyValue(writer: BufferedPrintWriter, value: Value, sensitivePropNames: Set[String],
	                         indentation: Int): Unit =
	{
		// Case: Empty value => Prints null
		if (value.isEmpty)
			writer.println("null")
		// Case: Model value => Lists all its properties
		else if (value.isOfType(ModelType)) {
			val model = value.getModel
			if (model.isEmpty)
				writer.println("{ }")
			else {
				val indent = "\t" * (indentation + 1)
				writer.println("{")
				value.getModel.nonEmptyPropertiesIterator.foreach { prop =>
					// Case: Sensitive property => Omit its value
					if (prop.value.nonEmpty && sensitivePropNames.contains(prop.name.toLowerCase))
						writer.println(s"$indent${ prop.name }: <omitted>")
					else {
						writer.print(indent)
						writer.print(prop.name)
						writer.print(": ")
						// Writes the value recursively
						logBodyValue(writer, prop.value, sensitivePropNames, indentation + 1)
					}
				}
				writer.println(s"${ "\t" * indentation }}")
			}
		}
		// Case: Vector type value => Lists all its elements using recursion
		else if (value.isOfType(VectorType) || value.isOfType(PairType)) {
			val values = value.getVector
			if (values.isEmpty)
				writer.println("[]")
			else
				values.iterator.zipWithIndex.foreach { case (elem, index) =>
					writer.print(s"\n${ "\t" * (indentation + 1) }${ index + 1 }. ")
					logBodyValue(writer, elem, sensitivePropNames, indentation + 1)
					writer.println()
				}
		}
		// Case: Basic value => Prints it as a JSON value
		else
			writer.println(value.toJson)
	}
	
	private def recordResponse(nodeName: String, method: Method, statusCode: Int,
	                           responseDoc: Option[DetailedPossibleResponse], responseBodyStr: String,
	                           responseBodyValue: Value): Unit =
		responseDoc match {
			// Case: Documentation exists => Records the response to the DB
			case Some(doc) =>
				cPool.logging { implicit c =>
					implicit val updateId: DocUpdateId = DocUpdateId.latest(apiId)
					ExamplesDb.storeResponseExample(nodeName, method, doc, responseBodyValue, responseBodyStr)
				}
			// Case: No documentation => Records a local file containing the received response
			case None =>
				Paths
					.get(s"examples/unimplemented/${
						nodeName.toLowerCase.replace(" ", "-") }-$method-$statusCode-$Today-1.json")
					.createParentDirectories()
					.flatMap { path => path.unique.write(responseBodyStr) }
					.logWith("Failed to write a response body example")
		}
	
	private def standardizeRef(str: String) = str.toLowerCase.filter { _.isLetterOrDigit }
}
