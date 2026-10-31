package vf.myapidoc.database.store

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.Today
import utopia.vault.database.Connection
import vf.myapidoc.database.access.example.prop.AccessExamplePropertyValues
import vf.myapidoc.database.access.example.response.AccessExampleResponses
import vf.myapidoc.database.storable.example.{ExamplePropertyValueDbModel, ExampleResponseDbModel}
import vf.myapidoc.database.value.DocUpdateId
import vf.myapidoc.model.combined.api.DetailedPossibleResponse
import vf.myapidoc.model.combined.schema.DetailedPropertySchema
import vf.myapidoc.model.partial.example.{ExamplePropertyValueData, ExampleResponseData}
import vf.myapidoc.util.Common._

import java.nio.file.Path
import scala.util.Success

/**
 * Used for interacting with API response examples
 * @author Mikko Hilpinen
 * @since 30.10.2026, v0.1
 */
object ExamplesDb
{
	/**
	 * Stores a response example to the DB, if needed
	 * @param nodeName Name of the node which this request concerns. Used in the generated file path, if needed.
	 * @param method Method that was used. Used in the generated file path, if needed.
	 * @param doc Matching response documentation (same node, same method, same status code)
	 * @param body Received response body as a value
	 * @param rawBody Received response body as a string. May be written into a local file.
	 * @param connection Implicit DB connection
	 * @param docId Implicit latest documentation version ID.
	 * @return Returns either:
	 *         - Success(Some((Stored example response, Stored example values))) if data was stored
	 *         - Success(None) if storing was skipped because there already existed enough examples.
	 *         - Failure if failed to write the response body into a local file
	 */
	def storeResponseExample(nodeName: => String, method: => Method, doc: DetailedPossibleResponse, body: Value,
	                         rawBody: => String)
	                        (implicit connection: Connection, docId: DocUpdateId) =
	{
		// Matches the received properties with the expected model schema
		val matchedValues = doc.body
			.flatMap { bodySchema =>
				bodySchema.modelType.map { contentSchema =>
					val matchesIter = {
						if (bodySchema.isArray)
							body.getVector.iterator.flatMap { _.model }
								.flatMap { matchPropertyValues(contentSchema.properties, _) }
						else
							body.model match {
								case Some(receivedBody) => matchPropertyValues(contentSchema.properties, receivedBody)
								case None => Iterator.empty
							}
					}
					// Only includes up to 3 distinct values per property
					matchesIter.groupMapMap { _._1.id } { _._2 } { _.iterator.distinct.take(3).toOptimizedSeq }
						.withDefaultValue(Empty)
				}
			}
			.getOrElse { Map[Int, Seq[Value]]().withDefaultValue(Empty) }
		
		// Pulls all existing property value examples
		val existingExamples = AccessExamplePropertyValues.ofProperties(matchedValues.keys).valuesPerProp
		
		// Finds properties that could benefit from the additional values
		val examplesToAddIter = matchedValues.iterator
			.flatMap { case (propId, newValues) =>
				val valuesToAddIter = existingExamples.get(propId) match {
					case Some(existingValues) =>
						if (existingValues.size < 3)
							newValues.iterator.filter { v => !existingValues.exists { _ ~== v } }
								.take(3 - existingValues.size)
						else
							Iterator.empty
					case None => newValues.iterator
				}
				valuesToAddIter.map { propId -> _ }
			}
		
		// Checks whether it's appropriate to insert a new response example
		// Case: New examples are appropriate
		//       => Records the new response example and the selected property value examples
		if (examplesToAddIter.hasNext || AccessExampleResponses.matchingStatus(doc.id).size < 3) {
			insertResponseExample(nodeName, method, doc, rawBody).map { storedExample =>
				val storedPropExamples = ExamplePropertyValueDbModel.insert(examplesToAddIter
					.map { case (propId, value) => ExamplePropertyValueData(storedExample.id, propId, value) }
					.toOptimizedSeq)
				
				Some(storedExample -> storedPropExamples)
			}
		}
		// Case: No further examples are needed => Skips the insert
		else
			Success(None)
	}
	
	private def insertResponseExample(nodeName: => String, method: => Method, doc: DetailedPossibleResponse,
	                                  rawBody: String)
	                                 (implicit connection: Connection, docId: DocUpdateId) =
	{
		val relativePath: Path =
			(s"responses/${ nodeName.toLowerCase.replace(" ", "-") }/$method-${ doc.statusCode }-$Today.json": Path)
				.unique
		(examplesDirectory/relativePath).write(rawBody).map { _ =>
			ExampleResponseDbModel.insert(ExampleResponseData(docId.connectedValue, doc.id, relativePath))
		}
	}
	
	/**
	 * Matches read properties with property schemas
	 * @param properties Property schemas
	 * @param body Read model entry
	 * @return An iterator that yields all properties that could be matched with a non-empty value.
	 *         Each entry contains:
	 *              1. Matched property
	 *              1. Matching value
	 *
	 *         Multiple values may be included for a single property. May include nested properties.
	 */
	private def matchPropertyValues(properties: Iterable[DetailedPropertySchema],
	                                body: Model): Iterator[(DetailedPropertySchema, Value)] =
		properties.iterator.flatMap { prop =>
			// Reads the matching value
			val value = body(prop.name)
			// Case: Empty value => Skips this property
			if (value.isEmpty)
				Empty
			else
				prop.contentType match {
					// Case: This property is expected to contain one or more objects
					case Some(contentSchema) =>
						// Case: Object properties have not been specified => Skips this property
						if (contentSchema.properties.isEmpty)
							Empty
						// Case: Multiple values may be received at once
						//       => Handles each one separately by matching it with the nested model schema
						else if (prop.isArray.mayBeTrue)
							value.getVector.iterator.flatMap { _.model }
								.flatMap { matchPropertyValues(contentSchema.properties, _) }
						// Case: Only a single value may be received
						//       => Attempts to match it with the nested model schema
						else
							value.model.iterator.flatMap { matchPropertyValues(contentSchema.properties, _) }
					
					// Case: Basic value property => Links the read value to it
					case None =>
						// Case: May include multiple values => Links each one separately
						if (prop.isArray.mayBeTrue)
							value.getVector.iterator.filter { _.nonEmpty }.map { prop -> _ }
						// Case: Only a single value may be received => Links it
						else
							Single(prop -> value)
				}
		}
}
