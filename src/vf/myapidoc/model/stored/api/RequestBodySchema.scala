package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.api.body.AccessRequestBodySchema
import vf.myapidoc.model.factory.api.RequestBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.{BodySchemaData, RequestBodySchemaData}

object RequestBodySchema extends StandardStoredFactory[RequestBodySchemaData, RequestBodySchema]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = RequestBodySchemaData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new request body schema
	  * @param id   ID of this request body schema in the database
	  * @param data Wrapped request body schema data
	  * @return request body schema with the specified id and wrapped data
	  */
	def apply(id: Int, data: RequestBodySchemaData): RequestBodySchema = _RequestBodySchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the request body schema trait
	  * @param id   ID of this request body schema in the database
	  * @param data Wrapped request body schema data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _RequestBodySchema(id: Int, data: RequestBodySchemaData) extends RequestBodySchema
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: RequestBodySchemaData) = copy(data = data)
	}
}

/**
  * Represents a request body schema that has already been stored in the database. 
  * Applies a specific schema to the request body accepted by an API node. 
  * Multiple alternative schemas may be defined separately.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait RequestBodySchema 
	extends RequestBodySchemaFactoryWrapper[RequestBodySchemaData, RequestBodySchema] with BodySchemaData 
		with StoredBodySchemaLike[RequestBodySchemaData, RequestBodySchema]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this request body schema in the database
	  */
	def access = AccessRequestBodySchema(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

