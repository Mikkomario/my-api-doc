package vf.myapidoc.database.store

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, EmptyView, OptimizedIndexedSeq}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.operator.MaybeEmpty
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
import vf.myapidoc.database.value.DocUpdateId
import vf.myapidoc.model.cached.doc.{NewNamedModel, NewProperty}
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
				.pullAndGroupBy { _.propId },
			possiblePropValues = AccessPossiblePropertyValues.active.whereActiveModels.inApi(apiId)
				.pullAndGroupBy { _.propId })
			.toOptimizedSeq
	
	/**
	 * Updates a named model's entry. Assumes that no breaking changes are needed!
	 * @param matches Non-conflicting matches between existing and new model versions
	 * @param modelNameToId A **mutable** map where keys are standardized model type names and values are model type IDs.
	 *                      This map is extended to include this model.
	 * @param connection Implicit DB connection
	 * @return Returns two values:
	 *         1. Existing model schemas
	 *         1. A function that resolves all delayed property inserts using a completed modelNameToId -map.
	 */
	def updateNamed(matches: Iterable[(NewNamedModel, CompleteModelSchema)], modelNameToId: mutable.Map[String, Int])
	               (implicit connection: Connection, update: DocUpdateId): (Seq[StoreResult[CompleteModelSchema]], collection.Map[String, Int] => Set[String]) =
	{
		matches.foreach { case (model, existing) => modelNameToId(standardizeRef(model.name)) = existing.id }
		val (updated, delayedInserts) = matches.splitMap { case (model, existing) =>
			StoreResult.existed(existing) -> _update(existing, model.properties, modelNameToId)
		}
		updated -> delayedInserts.reduceLeftOption { _ ++ _ }.getOrElse(DelayedPropInserts.empty).resolveUsing
	}
	/**
	 * Updates a nameless model's entry by updating the wrapped properties.
	 * @param existing An existing model schema entry
	 * @param properties Properties in the updated version
	 * @param modelNameToId A map where keys are standardized model type names and values are model type IDs.
	 *                      Expected to contain all named model types in the targeted API.
	 * @param connection Implicit DB connection
	 * @param update Update that introduces these changes
	 */
	def updateNameless(existing: CompleteModelSchema, properties: IterableOnce[NewProperty],
	                   modelNameToId: collection.Map[String, Int])
	                  (implicit connection: Connection, update: DocUpdateId): Unit =
	{
		val delayedInserts = _update(existing, properties, modelNameToId)
		val unresolvedReferences = delayedInserts.resolveUsing(modelNameToId)
		
		// Case: Some model type references couldn't be resolved => Logs and inserts without a model reference
		if (unresolvedReferences.nonEmpty)
			scribe.in("update.model.nameless.unresolvedReferences").warning(
				"Some model references couldn't be resolved", Model.from(
					"unresolved" -> unresolvedReferences.toVector.sorted))
	}
	
	def insertNamelessUseCase(params: Iterable[NewProperty], modelNameToId: Map[String, Int])
	                         (linkUsage: Int => Unit)
	                         (implicit connection: Connection, update: DocUpdateId) =
	{
		val result = insertNamelessCompletelyIfNecessary(params, modelNameToId)
		// Links the model to the node/implementation
		result.foreach { schema => linkUsage(schema.id) }
		
		result
	}
	def insertNamelessCompletelyIfNecessary(params: Iterable[NewProperty],
	                                        modelNameToId: collection.Map[String, Int])
	                                       (implicit connection: Connection, update: DocUpdateId): Option[ModelSchema] =
	{
		// Case: No path parameters to attach
		if (params.isEmpty)
			None
		else {
			// Inserts a nameless model for these parameters
			val model = ModelSchemaDbModel.insert(ModelSchemaData(update.connectedValue, update.apiId))
			// Attaches the parameters
			params.iterator.map { tryInsertProperty(model.id, _, modelNameToId) }
				.reduceOption { _ ++ _ }
				.foreach { delayedInserts =>
					val unresolvedReferences = delayedInserts.resolveUsing(modelNameToId)
					if (unresolvedReferences.nonEmpty)
						scribe.in("insert.nameless.completely.unresolvedReferences").warning(
							"Some model type references couldn't be resolved",
							Model.from("unresolved" -> unresolvedReferences.toVector.sorted))
				}
			
			Some(model)
		}
	}
	/**
	 * Inserts 0-n named model schemas
	 * @param models Models to insert
	 * @param modelNameToId A mutable map where keys are standardized model names and values are matching model IDs.
	 * @param connection Implicit DB connection
	 * @param update Update that introduces these changes
	 * @return Inserted models
	 */
	def insertNamed(models: IterableOnce[NewNamedModel], modelNameToId: mutable.Map[String, Int])
	               (implicit connection: Connection, update: DocUpdateId) =
	{
		val (inserted, delayed) = models.iterator.splitMap { model =>
			val result = _insert(model.properties, model.name, model.description, modelNameToId)
			modelNameToId += (standardizeRef(model.name) -> result._1.id)
			result
		}
		// Handles unresolved model references
		delayed.reduceOption { _ ++ _ }.foreach { delayed =>
			val unresolved = delayed.resolveUsing(modelNameToId)
			if (unresolved.nonEmpty)
				scribe.in("insert.named.unresolvedReferences").warning(
					"Some model type references couldn't be resolved",
					Model.from("unresolved" -> unresolved.toVector.sorted))
		}
		inserted
	}
	private def _insert(properties: Seq[NewProperty], name: String, description: String,
	                    modelNameToId: collection.Map[String, Int])
	                   (implicit connection: Connection, update: DocUpdateId): (ModelSchema, DelayedPropInserts) =
	{
		// Inserts the model schema to the DB
		val stored = ModelSchemaDbModel.insert(ModelSchemaData(update.connectedValue, update.apiId,
			name = name, description = description))
		
		// Inserts the properties, where possible
		val delayedPropInserts = properties.map { tryInsertProperty(stored.id, _, modelNameToId) }
			.reduceOption { _ ++ _ }.getOrElse(DelayedPropInserts.empty)
		
		stored -> delayedPropInserts
	}
	
	private def _update(existing: CompleteModelSchema, properties: IterableOnce[NewProperty],
	                    modelNameToId: collection.Map[String, Int])
	                   (implicit connection: Connection, update: DocUpdateId): DelayedPropInserts =
	{
		// Handles the property updates
		val lazyExistingContentTypes = Lazy {
			AccessModelSchemas(existing.properties.iterator.flatMap { _.contentTypeId }).toMapBy { _.id }
		}
		lazy val existingPropIds = existing.properties.view.map { _.id }.toIntSet
		val lazyExistingAltTypes = Lazy {
			AccessAlternativePropertyTypes.active.ofProperties(existingPropIds).pullAndGroupBy { _.propId }
		}
		val lazyPossibleValues = Lazy {
			AccessPossiblePropertyValues.active.ofProperties(existingPropIds).pullAndGroupBy { _.propId }
		}
		
		updateExclusiveMatches(existing.properties, properties) { _.name } { _.name } {
			(existingProp, prop) =>
				tryUpdateProperty(existingProp, existingAltTypes = lazyExistingAltTypes.value(existingProp.id),
					existingContent = existingProp.contentTypeId.flatMap { lazyExistingContentTypes.value.get(_) },
					existingPossibleValues =  lazyPossibleValues.value(existingProp.id), property = prop,
					modelNameToId = modelNameToId)
		} {
			propIds =>
				AccessPropertySchemas(propIds).deprecate()
				AccessModelSchemas.active.nameless.whereReferencingProperties.in(propIds).deprecate()
		} {
			newProps => newProps.map { tryInsertProperty(existing.id, _, modelNameToId) }
		}.reduceOption { _ ++ _ }.getOrElse(DelayedPropInserts.empty)
	}
	
	private def tryUpdateProperty(existing: StoredPropertySchema,
	                              existingAltTypes: => Iterable[AlternativePropertyType],
	                              existingContent: => Option[ModelSchema],
	                              existingPossibleValues: => IterableOnce[PossiblePropertyValue], property: NewProperty,
	                              modelNameToId: collection.Map[String, Int])
	                             (implicit connection: Connection, update: DocUpdateId): DelayedPropInserts =
	{
		val modelRef = standardizeRef(property.referencedType)
		lazy val _existingContent = existingContent
		lazy val _existingNamelessContent = _existingContent.filter { _.isNameless }.map { schema =>
			CompleteModelSchema(schema, AccessPropertySchemas.active.ofModelType(schema.id).pull)
		}
		
		// Checks whether a completely new property version should be introduced
		val shouldReplace = {
			// Case: Basic information matches => Checks whether the referenced model type is compatible
			if (existing.data ~== property)
				_existingContent match {
					case Some(existingContent) =>
						// Case: Was named model
						//       => Checks whether the reference changed or whether the model ID changed (if known)
						if (existingContent.hasName)
							modelRef.isEmpty || modelNameToId.get(modelRef).exists { _ != existingContent.id }
						// Case: Was nameless model
						//       => Checks whether a named reference was introduced or nameless content removed
						else
							modelRef.nonEmpty || property.children.isEmpty
					
					// Case: Had no nested content => Checks whether nested content was introduced
					case None => modelRef.nonEmpty || property.children.nonEmpty
				}
			// Case: Direct conflict => Replaces
			else
				true
		}
		def deprecateExisting() = {
			existing.access.deprecate()
			AccessModelSchemas.active.nameless.whereReferencingProperties.withId(existing.id).deprecate()
		}
		
		// Case: Replacing the existing property version => Deprecates it and inserts a new version
		if (shouldReplace) {
			deprecateExisting()
			tryInsertProperty(existing.modelTypeId, property, modelNameToId)
		}
		// Case: Referenced model type can't be resolved yet
		//       => Delays this update, since an ID change may introduce a conflict
		else if (modelRef.nonEmpty && !modelNameToId.contains(modelRef))
			DelayedPropInserts(modelRef, property.referencedType) { referencedModelTypeId: Option[Int] =>
				// Case: No conflict => Updates the property
				if (existing.contentTypeId == referencedModelTypeId)
					updateProperty(existing, existingAltTypes, existingPossibleValues, _existingNamelessContent,
						property, modelNameToId)
				// Case: Model type ID was updated => Deprecates the old version and inserts a new property
				else {
					deprecateExisting()
					insertProperty(existing.modelTypeId, property, referencedModelTypeId)
					DelayedPropInserts.empty
				}
			}
		// Case: Referenced model type can be resolved => Proceeds to update the property
		else
			updateProperty(existing, existingAltTypes, existingPossibleValues, _existingNamelessContent,
				property, modelNameToId)
	}
	// Assumes that the properties match and don't conflict
	private def updateProperty(existing: StoredPropertySchema, existingAltTypes: Iterable[AlternativePropertyType],
	                           existingPossibleValues: IterableOnce[PossiblePropertyValue],
	                           existingNamelessContent: Option[CompleteModelSchema], property: NewProperty,
	                           modelNameToId: collection.Map[String, Int])
	                          (implicit connection: Connection, update: DocUpdateId): DelayedPropInserts =
	{
		// Updates the property description
		if (existing.data.description != property.description)
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
				.map { t => AlternativePropertyTypeData(update.connectedValue, existing.id, altType = t) }
				.toOptimizedSeq)
		
		// Updates possible enum values
		updateExclusiveMatches(existingPossibleValues, property.possibleValues) { _.value } { _._1 } {
			case (existing, (_, description)) =>
				if (existing.data.description != description)
					existing.access.description.set(description)
				StoreResult.existed(existing)
				
		} { AccessPossiblePropertyValues(_).deprecate() } {
			newValues =>
				PossiblePropertyValueDbModel
					.insert(newValues.map { case (value, description) =>
						PossiblePropertyValueData(update.connectedValue, existing.id, value, description = description)
					})
					.map {
						//noinspection ConvertibleToMethodValue
						StoreResult.inserted(_)
					}
		}
		
		// Updates the nested properties
		if (property.children.nonEmpty)
			existingNamelessContent match {
				case Some(existingContent) => _update(existingContent, property.children, modelNameToId)
				case None =>
					val (modelType, delayedInserts) = _insert(property.children, "", "", modelNameToId)
					existing.access.modelTypeId.set(modelType.id)
					delayedInserts
			}
		else
			DelayedPropInserts.empty
	}
	
	/**
	 * Attempts to insert a property to the DB.
	 * Delays the insert if a model reference cannot be resolved.
	 * @param modelId ID of the model that contains this property
	 * @param property Property to insert
	 * @param modelNameToId A map that maps model type IDs to standardized model type names
	 * @param connection Implicit DB connection
	 * @param update Update that introduces these changes
	 * @return Yields property inserts that were delayed
	 */
	private def tryInsertProperty(modelId: Int, property: NewProperty, modelNameToId: collection.Map[String, Int])
	                             (implicit connection: Connection, update: DocUpdateId): DelayedPropInserts =
	{
		// Inserts the nested nameless model type, if applicable
		if (property.children.isEmpty) {
			// Standardizes the referenced type name
			val ref = standardizeRef(property.referencedType)
			
			// Case: Property insert is not yet possible because the reference hasn't been resolved
			//       => Creates a function for inserting this property once the reference resolves
			if (ref.nonEmpty && !modelNameToId.contains(ref))
				DelayedPropInserts(ref, property.referencedType) { referencedTypeId =>
					insertProperty(modelId, property, referencedTypeId)
					DelayedPropInserts.empty
				}
			// Case: Property insert is possible => Performs it immediately
			else {
				insertProperty(modelId, property, ref.ifNotEmpty.map(modelNameToId.apply))
				DelayedPropInserts.empty
			}
		}
		else {
			val (nestedModelType, delayedNestedInserts) = _insert(property.children, "", "", modelNameToId)
			insertProperty(modelId, property, Some(nestedModelType.id))
			delayedNestedInserts
		}
	}
	private def insertProperty(modelId: Int, property: NewProperty, contentTypeId: Option[Int])
	                          (implicit connection: Connection, update: DocUpdateId) =
	{
		// Inserts the property to the DB
		val stored = PropertySchemaDbModel.insert(PropertySchemaData(originalUpdateId = update.connectedValue,
			modelTypeId = modelId, name = property.name, dataType = property.primaryType,
			contentTypeId = contentTypeId, description = property.description, isArray = property.isArray,
			isOptional = property.isOptional))
		
		// Registers the alternative property types, if applicable
		if (property.dataTypes.hasSize > 1)
			AlternativePropertyTypeDbModel.insert(property.dataTypes.tail.map { t =>
				AlternativePropertyTypeData(update.connectedValue, stored.id, altType = t)
			})
				
		// Registers possible enum values, if applicable
		PossiblePropertyValueDbModel.insert(property.possibleValues.map { case (value, description) =>
			PossiblePropertyValueData(update.connectedValue, stored.id, value, description = description)
		})
		
		stored
	}
	
	private def combine(remainingModels: IterableOnce[ModelSchema], completedModelsById: Map[Int, DetailedModelSchema],
	                    rawProps: Map[Int, Seq[StoredPropertySchema]],
	                    propAltTypes: Map[Int, Seq[AlternativePropertyType]],
	                    possiblePropValues: Map[Int, Seq[PossiblePropertyValue]]): scala.collection.View[DetailedModelSchema] =
	{
		// Attempts to combine as many models as possible
		val (delayedModels, completedModels) = remainingModels.divideWith { model =>
			val props = rawProps(model.id)
			// All model references must be resolved.
			// If not, the combining gets delayed.
			props.findForAll { prop =>
				// Case: Self-referential property => Omits the content type
				if (prop.contentTypeId.contains(model.id))
					Some(DetailedPropertySchema(prop.id, prop.data, altTypes = propAltTypes(prop.id),
						possibleValues = possiblePropValues(prop.id)))
				else if (prop.contentTypeId.forall(completedModelsById.contains))
					Some(DetailedPropertySchema(prop.id, prop.data, prop.contentTypeId.map(completedModelsById),
						propAltTypes(prop.id), possibleValues = possiblePropValues(prop.id)))
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
			completedModels.view ++ combine(delayedModels, nowCompletedModelsById, rawProps, propAltTypes,
				possiblePropValues)
		}
	}
	
	
	// NESTED   ----------------------------
	
	private object DelayedPropInserts {
		lazy val empty = new DelayedPropInserts(Empty)
		
		def apply(ref: String, name: String)(insert: Option[Int] => DelayedPropInserts) =
			new DelayedPropInserts(Seq((ref, name, insert)))
	}
	private class DelayedPropInserts(val delayed: Seq[(String, String, Option[Int] => DelayedPropInserts)])
		extends MaybeEmpty[DelayedPropInserts]
	{
		override def self: DelayedPropInserts = this
		override def isEmpty: Boolean = delayed.isEmpty
		
		def ++(other: DelayedPropInserts) = {
			if (other.isEmpty)
				this
			else if (isEmpty)
				other
			else
				new DelayedPropInserts(delayed ++ other.delayed)
		}
		
		def resolveUsing(modelNameToId: collection.Map[String, Int]) = {
			var nextIteration = delayed
			val unresolvedBuilder = OptimizedIndexedSeq.newBuilder[String]
			while (nextIteration.nonEmpty) {
				nextIteration = nextIteration.flatMap { case (ref, name, insert) =>
					val result = insert(modelNameToId.get(ref))
					if (!modelNameToId.contains(ref))
						unresolvedBuilder += name
					result.delayed
				}
			}
			unresolvedBuilder.result().toSet
		}
	}
}
