package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.api.BodySchemaData

object BodySchema extends StandardStoredFactory[BodySchemaData, BodySchema]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = BodySchemaData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new body schema
	  * @param id   ID of this body schema in the database
	  * @param data Wrapped body schema data
	  * @return body schema with the specified id and wrapped data
	  */
	def apply(id: Int, data: BodySchemaData): BodySchema = _BodySchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the body schema trait
	  * @param id   ID of this body schema in the database
	  * @param data Wrapped body schema data
	  * @author Mikko Hilpinen
	  * @since 27.10.2026
	  */
	private case class _BodySchema(id: Int, data: BodySchemaData) extends BodySchema
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: BodySchemaData) = copy(data = data)
	}
}

/**
  * Represents a body schema that has already been stored in the database. 
  * Common trait for request & response body schemas.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchema extends StoredBodySchemaLike[BodySchemaData, BodySchema] with BodySchemaData

