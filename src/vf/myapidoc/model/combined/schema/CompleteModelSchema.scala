package vf.myapidoc.model.combined.schema

import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.{ModelSchema, StoredPropertySchema}

object CompleteModelSchema
{
	// OTHER	--------------------
	
	/**
	  * @param model      The model to wrap
	  * @param properties properties to attach
	  * @return Combination of the specified model and property
	  */
	def apply(model: ModelSchema, properties: Seq[StoredPropertySchema]): CompleteModelSchema = 
		apply(model.id, model.data, properties)
	/**
	  * @param id         ID of this model in the DB
	  * @param data       The wrapped model data
	  * @param properties properties to attach
	  * @return A new model with the specified property included
	  */
	def apply(id: Int, data: ModelSchemaData, properties: Seq[StoredPropertySchema]): CompleteModelSchema = 
		_CompleteModelSchema(id, data, properties)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id         ID of this model in the DB
	  * @param data       The wrapped model data
	  * @param properties properties to attach
	  */
	private case class _CompleteModelSchema(id: Int, data: ModelSchemaData, properties: Seq[StoredPropertySchema])
		extends CompleteModelSchema
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: ModelSchemaData) = copy(data = factory)
	}
}

/**
  * Includes the declared properties in a model schema.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait CompleteModelSchema extends CompleteModelSchemaLike[StoredPropertySchema, CompleteModelSchema]

