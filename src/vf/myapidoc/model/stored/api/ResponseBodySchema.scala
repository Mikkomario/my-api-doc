package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.api.response.body.AccessResponseBodySchema
import vf.myapidoc.model.factory.api.ResponseBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.{BodySchemaData, ResponseBodySchemaData}

object ResponseBodySchema extends StandardStoredFactory[ResponseBodySchemaData, ResponseBodySchema]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ResponseBodySchemaData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new response body schema
	  * @param id   ID of this response body schema in the database
	  * @param data Wrapped response body schema data
	  * @return response body schema with the specified id and wrapped data
	  */
	def apply(id: Int, data: ResponseBodySchemaData): ResponseBodySchema = _ResponseBodySchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the response body schema trait
	  * @param id   ID of this response body schema in the database
	  * @param data Wrapped response body schema data
	  * @author Mikko Hilpinen
	  * @since 27.10.2026
	  */
	private case class _ResponseBodySchema(id: Int, data: ResponseBodySchemaData) extends ResponseBodySchema
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ResponseBodySchemaData) = copy(data = data)
	}
}

/**
  * Represents a response body schema that has already been stored in the database. 
  * Defines how an API node's response body is formed.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait ResponseBodySchema 
	extends ResponseBodySchemaFactoryWrapper[ResponseBodySchemaData, ResponseBodySchema] with BodySchemaData 
		with StoredBodySchemaLike[ResponseBodySchemaData, ResponseBodySchema]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this response body schema in the database
	  */
	def access = AccessResponseBodySchema(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

