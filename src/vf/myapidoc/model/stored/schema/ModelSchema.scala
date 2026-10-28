package vf.myapidoc.model.stored.schema

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.schema.model.AccessModelSchema
import vf.myapidoc.model.factory.schema.ModelSchemaFactoryWrapper
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike

object ModelSchema extends StandardStoredFactory[ModelSchemaData, ModelSchema]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ModelSchemaData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new model schema
	  * @param id   ID of this model schema in the database
	  * @param data Wrapped model schema data
	  * @return model schema with the specified id and wrapped data
	  */
	def apply(id: Int, data: ModelSchemaData): ModelSchema = _ModelSchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the model schema trait
	  * @param id   ID of this model schema in the database
	  * @param data Wrapped model schema data
	  * @author Mikko Hilpinen
	  * @since 15.10.2026
	  */
	private case class _ModelSchema(id: Int, data: ModelSchemaData) extends ModelSchema
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ModelSchemaData) = copy(data = data)
	}
}

/**
  * Represents a model schema that has already been stored in the database. 
  * Defines properties in an input or output model / JSON object.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ModelSchema 
	extends ModelSchemaFactoryWrapper[ModelSchemaData, ModelSchema] with VersionedDocElementData
		with StoredVersionedDocElementLike[ModelSchemaData, ModelSchema]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this model schema in the database
	  */
	def access = AccessModelSchema(id)
}

