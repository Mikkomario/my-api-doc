package vf.myapidoc.database.store

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.EmptyView
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.util.StringExtensions._
import utopia.scribe.core.controller.logging.Scribe
import utopia.vault.database.Connection
import vf.myapidoc.database.access.schema.model.AccessModelSchemas
import vf.myapidoc.database.access.schema.prop.AccessPropertySchemas
import vf.myapidoc.database.access.schema.prop.types.AccessAlternativePropertyTypes
import vf.myapidoc.model.combined.schema.{DetailedModelSchema, DetailedPropertySchema}
import vf.myapidoc.model.stored.schema.{AlternativePropertyType, ModelSchema, StoredPropertySchema}
import vf.myapidoc.util.Common.newScribe

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
