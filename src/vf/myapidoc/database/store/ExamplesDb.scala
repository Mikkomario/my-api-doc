package vf.myapidoc.database.store

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.Today
import utopia.vault.database.Connection
import vf.myapidoc.database.access.example.prop.AccessExamplePropertyValues
import vf.myapidoc.database.access.example.prop.undocumented.AccessUndocumentedProperties
import vf.myapidoc.database.access.example.response.AccessExampleResponses
import vf.myapidoc.database.storable.example.{ExamplePropertyValueDbModel, ExampleResponseDbModel, UndocumentedPropertyDbModel}
import vf.myapidoc.database.value.DocUpdateId
import vf.myapidoc.model.combined.api.DetailedPossibleResponse
import vf.myapidoc.model.combined.schema.{DetailedModelSchema, DetailedPropertySchema}
import vf.myapidoc.model.partial.example.{ExamplePropertyValueData, ExampleResponseData, UndocumentedPropertyData}
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
		// Also looks for received properties that didn't match the documentation
		val (matchedValues, undocumentedValues) = doc.body
			.flatMap { bodySchema =>
				bodySchema.modelType.map { contentSchema =>
					val (matchesIter, undocumentedIter) = {
						if (bodySchema.isArray) {
							val receivedModels = body.getVector.flatMap { _.model }
							val matchesIter = receivedModels.iterator.flatMap { matchPropertyValues(contentSchema, _) }
							val undocumentedIter = receivedModels.iterator
								.flatMap { findUndocumentedValues(contentSchema, _) }
							
							matchesIter -> undocumentedIter
						}
						else
							body.model match {
								case Some(receivedBody) =>
									matchPropertyValues(contentSchema, receivedBody) ->
										findUndocumentedValues(contentSchema, receivedBody)
								case None => Iterator.empty -> Iterator.empty
							}
					}
					// Only includes up to 3 or 5 distinct values per matched property
					val matchedValues = matchesIter
						.groupMapMap { _._1.id } { _._2 } { _.iterator.distinct.take(3).toOptimizedSeq }
						.withDefaultValue(Empty)
					val undocumentedValues = undocumentedIter
						.groupMapMap { case (model, prop, received) =>
							val propKey = prop match {
								case Some(prop) => Right(prop.id)
								case None => Left(received.name)
							}
							model.id -> propKey
						} { _._3.value } { _.iterator.distinct.take(5).toOptimizedSeq }
					
					matchedValues -> undocumentedValues
				}
			}
			// If no documentation existed, yields placeholder values
			.getOrElse { Map[Int, Seq[Value]]().withDefaultValue(Empty) -> Map() }
		
		// Finds properties that could benefit from the additional values
		val examplesToAddIter = {
			if (matchedValues.isEmpty)
				Iterator.empty
			else {
				val existingExamples = AccessExamplePropertyValues.documented.ofProperties(matchedValues.keys)
					.valuesPerProp
				matchedValues.iterator
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
			}
		}
		
		// Checks which of the undocumented cases have already been recorded
		val (missingRecordsIter, newUnexpectedValuesIter) = {
			// Case: No undocumented entries
			if (undocumentedValues.isEmpty)
				Iterator.empty -> Iterator.empty
			else {
				// Divides the undocumented cases into undocumented properties and unexpected values
				val (missingProperties, unexpectedValues) = undocumentedValues
					.divideWith { case ((modelId, propSchema), values) =>
						propSchema match {
							case Left(propName) => Left((modelId, propName, values))
							case Right(propId) => Right(propId -> values)
						}
					}
				val newMissingRecordsIter = {
					// Case: No undocumented properties
					if (missingProperties.isEmpty)
						Iterator.empty
					// Case: Undocumented properties were encountered
					//       => Pulls existing cases and checks whether new inserts are appropriate
					//          (limits up to 5 examples per property per API version)
					else {
						val existing = AccessUndocumentedProperties
							.inModels(missingProperties.iterator.map { _._1 })
							.whereResponses.fromCurrentApiVersion
							.valuesByProperty
						
						missingProperties.iterator.flatMap { case (modelId, name, values) =>
							existing.get(modelId -> name) match {
								case Some(existingValues) =>
									val capacity = 5 - existingValues.size
									if (capacity <= 0)
										Iterator.empty
									else
										values.iterator.filterNot { v => existingValues.exists { _ ~== v } }
											.take(capacity).map { (modelId, name, _) }
								
								case None => values.iterator.map { (modelId, name, _) }
							}
						}
					}
				}
				val newUndocumentedValuesIter = {
					// Case: No unexpected values
					if (unexpectedValues.isEmpty)
						Iterator.empty
					// Case: Unexpected values were encountered
					//       => Pulls existing examples and checks whether new inserts are appropriate
					//          (again, limits to 5 examples per property per API version)
					else {
						// WET WET
						val existing = AccessExamplePropertyValues.undocumented
							.ofProperties(unexpectedValues.iterator.map { _._1 })
							.whereResponses.fromCurrentApiVersion
							.valuesPerProp
						
						unexpectedValues.iterator.flatMap { case (propId, values) =>
							existing.get(propId) match {
								case Some(existingValues) =>
									val capacity = 5 - existingValues.size
									if (capacity <= 0)
										Iterator.empty
									else
										values.iterator.filterNot { v => existingValues.exists { _ ~== v } }
											.take(capacity).map { propId -> _ }
								
								case None => values.iterator.map { propId -> _ }
							}
						}
					}
				}
				
				(newMissingRecordsIter, newUndocumentedValuesIter)
			}
		}
		
		// Checks whether it's appropriate to insert a new response example
		// Case: New examples are appropriate
		//       => Records the new response example and the selected property examples
		if (examplesToAddIter.hasNext || missingRecordsIter.hasNext || newUnexpectedValuesIter.hasNext ||
			AccessExampleResponses.matchingStatus(doc.id).size < 3)
			insertResponseExample(nodeName, method, doc, rawBody).map { storedExample =>
				val storedPropExamples = ExamplePropertyValueDbModel.insert(
					(examplesToAddIter.map { case (propId, value) =>
						ExamplePropertyValueData(storedExample.id, propId, value, matchedDocumentation = true)
					} ++ newUnexpectedValuesIter.map { case (propId, value) =>
						ExamplePropertyValueData(storedExample.id, propId, value)
					})
					.toOptimizedSeq)
				val storedUndocumentedProps = UndocumentedPropertyDbModel.insert(missingRecordsIter
					.map { case (modelId, propName, value) =>
						UndocumentedPropertyData(storedExample.id, modelId, propName, value)
					}
					.toOptimizedSeq)
				
				Some((storedExample, storedPropExamples, storedUndocumentedProps))
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
	 * @param schema Model schema from the documentation
	 * @param body Read model entry
	 * @return An iterator that yields all properties that could be matched with a non-empty value.
	 *         Each entry contains:
	 *              1. Matched property
	 *              1. Matching value
	 *
	 *         Multiple values may be included for a single property. May include nested properties.
	 */
	private def matchPropertyValues(schema: DetailedModelSchema, body: Model): Iterator[(DetailedPropertySchema, Value)] = {
		// Skips sensitive properties
		schema.properties.iterator.filter { _.isNotSensitive }.flatMap { prop =>
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
								.flatMap { matchPropertyValues(contentSchema, _) }
						// Case: Only a single value may be received
						//       => Attempts to match it with the nested model schema
						else
							value.model.iterator.flatMap { matchPropertyValues(contentSchema, _) }
					
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
	
	/**
	 * Finds all instances where a received value differs from the documentation
	 * @param schema Documented model schema
	 * @param body Received model
	 * @return An iterator that yields all cases where the received property didn't match the documentation.
	 *         Each entry contains 3 values:
	 *         1. Model schema this difference concerns
	 *         1. Property schema this difference concerns. None if this property was not documented.
	 *         1. Received property
	 */
	private def findUndocumentedValues(schema: DetailedModelSchema, body: Model): Iterator[(DetailedModelSchema, Option[DetailedPropertySchema], Constant)] =
		body.propertiesIterator.flatMap { prop =>
			schema.properties.find { _.name ~== prop.name } match {
				case Some(matchingSchema) =>
					if ((matchingSchema.isRequired && prop.isEmpty) ||
						(prop.nonEmpty && !matchingSchema.dataType.accepts(prop.value)))
						Single((schema, Some(matchingSchema), prop))
					else
						matchingSchema.contentType match {
							case Some(contentSchema) =>
								prop.value.model match {
									case Some(model) => findUndocumentedValues(contentSchema, model)
									case None => Empty
								}
							case None => Empty
						}
				case None => Single((schema, None, prop))
			}
		}
}
