package vf.myapidoc.database.store

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.EmptyView
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.view.immutable.caching.Lazy
import utopia.scribe.core.controller.logging.Scribe
import utopia.vault.database.Connection
import utopia.vault.store.StoreResult
import vf.myapidoc.database.access.schema.model.AccessModelSchemas
import vf.myapidoc.database.access.schema.prop.AccessPropertySchemas
import vf.myapidoc.database.access.schema.prop.types.AccessAlternativePropertyTypes
import vf.myapidoc.database.access.schema.prop.value.AccessPossiblePropertyValues
import vf.myapidoc.database.storable.schema.{AlternativePropertyTypeDbModel, ModelSchemaDbModel, PossiblePropertyValueDbModel, PropertySchemaDbModel}
import vf.myapidoc.model.cached.{NewNamedModel, NewProperty}
import vf.myapidoc.model.combined.schema.{CompleteModelSchema, DetailedModelSchema, DetailedPropertySchema}
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.partial.schema.{AlternativePropertyTypeData, ModelSchemaData, PossiblePropertyValueData, PropertySchemaData}
import vf.myapidoc.model.stored.schema.{AlternativePropertyType, ModelSchema, PossiblePropertyValue, StoredPropertySchema}
import vf.myapidoc.util.Common.newScribe

import scala.collection.mutable

/**
 * Provides functions for interacting with model schemas
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
object ModelSchemaDb
{
	// ATTRIBUTES	---------------------
	
	private implicit val scribe: Scribe = newScribe("DB.ModelSchema")
	
	
	// OTHER    -------------------------
	
	/**
	 * Pulls all model schemas for a single API. Includes full property details.
	 * @param apiId ID of the targeted API
	 * @param connection Implicit DB connection
	 * @return All models in the specified API
	 */
	def pullApiModels(apiId: Int)(implicit connection: Connection) =
		combine(remainingModels = AccessModelSchemas.active.inApi(apiId).pull, completedModelsById = Map(),
			rawProps = AccessPropertySchemas.active.whereActiveModels.inApi(apiId).pullAndGroupBy { _.modelTypeId },
			propAltTypes = AccessAlternativePropertyTypes.active.whereActiveModels.inApi(apiId)
				.pullAndGroupBy { _.propId })
			.toOptimizedSeq
	
	/**
	 * Updates a named model's entry. Assumes that no breaking changes are needed!
	 * @param existing Existing version
	 * @param model Updated / latest version
	 * @param modelNameToId A **mutable** map where keys are standardized model type names and values are model type IDs.
	 *                      This map is extended to include this model.
	 * @param connection Implicit DB connection
	 * @return Returns the delayed property inserts (due to missing 'modelNameToId' entries).
	 *         Each of these contains:
	 *         1. Missing model type key (standardized)
	 *         1. Missing model type name (as it appears in the input)
	 *         1. A function that receives the ID of the referenced model type (or None)
	 *            and inserts the missing property.
	 *              - If the reference can't be resolved,
	 *                pass None to ignore the missing reference and to insert the property without it.
	 */
	def updateNamed(existing: CompleteModelSchema, model: NewNamedModel, modelNameToId: mutable.Map[String, Int])
	               (implicit connection: Connection) =
	{
		modelNameToId(standardizeRef(model.name)) = existing.id
		_update(existing, model.properties, modelNameToId)
	}
	/**
	 * Updates a nameless model's entry by updating the wrapped properties.
	 * @param existing An existing model schema entry
	 * @param properties Properties in the updated version
	 * @param modelNameToId A map where keys are standardized model type names and values are model type IDs.
	 *                      Expected to contain all named model types in the targeted API.
	 * @param connection Implicit DB connection
	 */
	def updateNameless(existing: CompleteModelSchema, properties: IterableOnce[NewProperty],
	                   modelNameToId: collection.Map[String, Int])
	                  (implicit connection: Connection): Unit =
	{
		val delayedInserts = _update(existing, properties, modelNameToId)
		// Case: Some model type references couldn't be resolved => Logs and inserts without a model reference
		if (delayedInserts.nonEmpty) {
			scribe.in("update.model.nameless.unresolvedReferences").recoverable(
				"Some model references couldn't be resolved", Model.from(
					"missingModelTypes" -> delayedInserts.map { _._2 }))
			delayedInserts.foreach { _._3(None) }
		}
	}
	
	def insertNamelessUseCase(apiId: Int, params: Iterable[NewProperty], modelNameToId: Map[String, Int])
	                         (linkUsage: Int => Unit)
	                         (implicit connection: Connection) =
	{
		val result = insertNameless(apiId, params, modelNameToId)
		// Links the model to the node/implementation
		result.foreach { schema => linkUsage(schema.id) }
		
		result
	}
	def insertNameless(apiId: Int, params: Iterable[NewProperty], modelNameToId: Map[String, Int])
	                  (implicit connection: Connection) =
	{
		// Case: No path parameters to attach
		if (params.isEmpty)
			None
		else {
			// Inserts a nameless model for these parameters
			val model = ModelSchemaDbModel.insert(ModelSchemaData(apiId))
			// Attaches the parameters
			params.foreach { p =>
				// TODO: Log if referenced model type can't be resolved
				insertProperty(model.id, _, referencedModelTypeId = p.referencedType.ifNotEmpty.flatMap { n =>
					modelNameToId.get(standardizeRef(n))
				})
			}
			Some(model)
		}
	}
	def insert(apiId: Int, model: NewNamedModel, modelNameToId: mutable.Map[String, Int])
	          (implicit connection: Connection) =
	{
		// Inserts the model schema to the DB
		val stored = ModelSchemaDbModel.insert(ModelSchemaData(apiId, model.name, model.description))
		val referenceKey = standardizeRef(model.name)
		modelNameToId += (referenceKey -> stored.id)
		
		// Inserts the properties, where possible
		val delayedPropInserts = model.properties.flatMap { tryInsertProperty(stored.id, _, modelNameToId).leftOption }
		
		StoreResult.inserted(stored) -> delayedPropInserts
	}
	
	private def _update(existing: CompleteModelSchema, properties: IterableOnce[NewProperty],
	                    modelNameToId: collection.Map[String, Int])
	                   (implicit connection: Connection) =
	{
		// Handles the property updates
		val lazyAlreadyReferencedModelNames = Lazy {
			AccessModelSchemas(existing.properties.iterator.flatMap { _.contentTypeId }).idToName
				.view.mapValues(standardizeRef).toMap
		}
		lazy val existingPropIds = existing.properties.view.map { _.id }.toIntSet
		val lazyExistingAltTypes = Lazy {
			AccessAlternativePropertyTypes.active.ofProperties(existingPropIds).pullAndGroupBy { _.propId }
		}
		val lazyPossibleValues = Lazy {
			AccessPossiblePropertyValues.active.ofProperties(existingPropIds).pullAndGroupBy { _.propId }
		}
		
		val delayedInserts = updateExclusiveMatches(existing.properties, properties) { _.name } { _.name } {
			(existing, prop) =>
				tryUpdateProperty(existing, existingAltTypes = lazyExistingAltTypes.value(existing.id),
					existingModelTypeReference = existing.contentTypeId match {
						case Some(referencedTypeId) =>
							lazyAlreadyReferencedModelNames.value.getOrElse(referencedTypeId, "???")
						case None => ""
					},
					existingPossibleValues =  lazyPossibleValues.value(existing.id), property = prop,
					modelNameToId = modelNameToId)
					.leftOption
		} { AccessPropertySchemas(_).deprecate() } { newProps =>
			newProps.map { tryInsertProperty(existing.id, _, modelNameToId).leftOption }
		}.flatten
		
		delayedInserts
	}
	
	private def tryUpdateProperty(existing: StoredPropertySchema,
	                              existingAltTypes: => Iterable[AlternativePropertyType],
	                              existingModelTypeReference: => String,
	                              existingPossibleValues: => IterableOnce[PossiblePropertyValue], property: NewProperty,
	                              modelNameToId: collection.Map[String, Int])
	                             (implicit connection: Connection) =
	{
		val modelRef = standardizeRef(property.referencedType)
		
		// Checks whether a completely new property version should be introduced
		val shouldReplace = {
			if (existing.data ~== property) {
				// Case: No direct conflicts => Checks whether the referenced model ID was changed (if known)
				if (existingModelTypeReference == modelRef)
					existing.contentTypeId.exists { existingReferencedModelId =>
						modelNameToId.get(modelRef).exists { _ != existingReferencedModelId }
					}
				// Case: Changed reference name => Replaces
				else
					true
			}
			// Case: Direct conflict => Replaces
			else
				true
		}
		// Case: Replacing the existing property version => Deprecates it and inserts a new version
		if (shouldReplace) {
			existing.access.deprecate()
			tryInsertProperty(existing.modelTypeId, property, modelNameToId)
		}
		// Case: Referenced model type can't be resolved yet
		//       => Delays this update, since an ID change may introduce a conflict
		else if (modelRef.nonEmpty && !modelNameToId.contains(modelRef))
			Left(modelRef, property.referencedType, { referencedModelTypeId: Option[Int] =>
				// Case: No conflict => Updates the property
				if (existing.contentTypeId == referencedModelTypeId)
					updateProperty(existing, existingAltTypes, existingPossibleValues, property)
				// Case: Model type ID was updated => Deprecates the old version and inserts a new property
				else {
					existing.access.deprecate()
					insertProperty(existing.modelTypeId, property, referencedModelTypeId)
				}
			})
		// Case: Referenced model type can be resolved => Proceeds to update the property
		else
			Right(updateProperty(existing, existingAltTypes, existingPossibleValues, property))
	}
	// Assumes that the properties match and don't conflict
	private def updateProperty(existing: StoredPropertySchema, existingAltTypes: Iterable[AlternativePropertyType],
	                           existingPossibleValues: IterableOnce[PossiblePropertyValue],
	                           property: NewProperty)
	                          (implicit connection: Connection) =
	{
		// Updates the property description
		if (existing.description != property.description)
			existing.access.description.set(property.description)
		
		// Checks for alt type updates
		val newAltTypes = property.dataTypes.drop(1)
		// Deprecates alt types that are no longer applicable
		AccessAlternativePropertyTypes(
			existingAltTypes.iterator.filterNot { t => newAltTypes.contains(t.altType) }.map { _.id })
			.deprecate()
		// Inserts new alt types, if applicable
		AlternativePropertyTypeDbModel.insert(
			newAltTypes.iterator.filterNot { t => existingAltTypes.exists { _.altType == t } }
				.map { t => AlternativePropertyTypeData(existing.id, t) }.toOptimizedSeq)
		
		// Updates possible enum values
		updateExclusiveMatches(existingPossibleValues, property.possibleValues) { _.value } { _._1 } {
			case (existing, (_, description)) =>
				if (existing.description != description)
					existing.access.description.set(description)
				StoreResult.existed(existing)
				
		} { AccessPossiblePropertyValues(_).deprecate() } {
			newValues =>
				PossiblePropertyValueDbModel
					.insert(newValues.map { case (value, description) =>
						PossiblePropertyValueData(existing.id, value, description)
					})
					.map {
						//noinspection ConvertibleToMethodValue
						StoreResult.inserted(_)
					}
		}
		
		StoreResult.existed(existing)
	}
	
	/**
	 * Attempts to insert a property to the DB.
	 * Delays the insert if a model reference cannot be resolved.
	 * @param modelId ID of the model that contains this property
	 * @param property Property to insert
	 * @param modelNameToId A map that maps model type IDs to standardized model type names
	 * @param connection Implicit DB connection
	 * @return Yields either:
	 *         - Right: Stored property
	 *         - Left: Delayed insert, containing 3 values:
	 *              1. Missing model type reference (standardized)
	 *              1. Name of the missing model type, as written in this property
	 *              1. A function that receives the model type ID and yields the stored property.
	 *                 Accepts None as a backup, in case the reference can never be resolved.
	 */
	private def tryInsertProperty(modelId: Int, property: NewProperty, modelNameToId: collection.Map[String, Int])
	                             (implicit connection: Connection) =
	{
		// Standardizes the referenced type name
		val ref = standardizeRef(property.referencedType)
		
		// Case: Property insert is not yet possible because the reference hasn't been resolved
		//       => Creates a function for inserting this property once the reference resolves
		if (ref.nonEmpty && !modelNameToId.contains(ref))
			Left((ref, property.referencedType,
				{ referencedTypeId: Option[Int] => insertProperty(modelId, property, referencedTypeId) }))
		// Case: Property insert is possible => Performs it immediately
		else
			Right(insertProperty(modelId, property, ref.ifNotEmpty.map(modelNameToId.apply)))
	}
	private def insertProperty(modelId: Int, property: NewProperty, referencedModelTypeId: Option[Int])
	                          (implicit connection: Connection) =
	{
		// Inserts the property to the DB
		val stored = PropertySchemaDbModel.insert(PropertySchemaData(modelTypeId = modelId, name = property.name,
			dataType = property.dataTypes.headOption.getOrElse(AnyProp), contentTypeId = referencedModelTypeId,
			description = property.description, isArray = property.isArray, isOptional = property.isOptional))
		
		// Registers the alternative property types, if applicable
		if (property.dataTypes.hasSize > 1)
			AlternativePropertyTypeDbModel.insert(
				property.dataTypes.tail.map { AlternativePropertyTypeData(stored.id, _) })
				
		// Registers possible enum values, if applicable
		PossiblePropertyValueDbModel.insert(property.possibleValues.map { case (value, description) =>
			PossiblePropertyValueData(stored.id, value, description)
		})
		
		StoreResult.inserted(stored)
	}
	
	private def combine(remainingModels: IterableOnce[ModelSchema], completedModelsById: Map[Int, DetailedModelSchema],
	                    rawProps: Map[Int, Seq[StoredPropertySchema]],
	                    propAltTypes: Map[Int, Seq[AlternativePropertyType]]): scala.collection.View[DetailedModelSchema] =
	{
		// Attempts to combine as many models as possible
		val (delayedModels, completedModels) = remainingModels.divideWith { model =>
			val props = rawProps(model.id)
			// All model references must be resolved.
			// If not, the combining gets delayed.
			props.findForAll { prop =>
				if (prop.contentTypeId.forall(completedModelsById.contains))
					Some(DetailedPropertySchema(prop.id, prop.data, prop.contentTypeId.map(completedModelsById),
						propAltTypes(prop.id)))
				else
					None
			} match {
				case Some(props) => Right(DetailedModelSchema(model.id, model.data, props))
				case None => Left(model)
			}
		}
		
		// Case: No models were completed
		if (completedModels.isEmpty) {
			// Case: Nothing to complete anymore => Finishes
			if (delayedModels.isEmpty)
				EmptyView
			// Case: Some models remained incomplete => Logs and ignores the references
			else {
				scribe.in("combine.models").recoverable(
					"Couldn't resolve all model references. Cyclical loops involved?", Model.from(
						"impossibleModels" -> delayedModels.view
							.map[Value] { model =>
								model.name.ifNotEmpty match {
									case Some(name) => name
									case None => model.id
								}
							}.toVector))
				delayedModels.view.map { model =>
					val props = rawProps(model.id).map { prop =>
						DetailedPropertySchema(prop.id, prop.data, prop.contentTypeId.flatMap(completedModelsById.get),
							propAltTypes(prop.id))
					}
					DetailedModelSchema(model.id, model.data, props)
				}
			}
		}
		// Case: Complete => Returns
		else if (delayedModels.isEmpty)
			completedModels.view
		// Case: Some models remained incomplete => Continues using recursion
		else {
			val nowCompletedModelsById = completedModelsById ++ completedModels.iterator.map { m => m.id -> m }
			completedModels.view ++ combine(delayedModels, nowCompletedModelsById, rawProps, propAltTypes)
		}
	}
}
