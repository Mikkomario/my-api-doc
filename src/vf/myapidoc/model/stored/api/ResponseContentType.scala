package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.response.body.AccessResponseContentType
import vf.myapidoc.model.factory.api.ResponseContentTypeFactoryWrapper
import vf.myapidoc.model.partial.api.ResponseContentTypeData

@deprecated("Deprecated for removal. Replaced with ResponseBodySchema", "v0.1")
object ResponseContentType extends StandardStoredFactory[ResponseContentTypeData, ResponseContentType]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ResponseContentTypeData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new response content type
	  * @param id   ID of this response content type in the database
	  * @param data Wrapped response content type data
	  * @return response content type with the specified id and wrapped data
	  */
	def apply(id: Int, data: ResponseContentTypeData): ResponseContentType = _ResponseContentType(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the response content type trait
	  * @param id   ID of this response content type in the database
	  * @param data Wrapped response content type data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _ResponseContentType(id: Int, 
		data: ResponseContentTypeData) extends ResponseContentType
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ResponseContentTypeData) = copy(data = data)
	}
}

/**
  * Represents a response content type that has already been stored in the database. 
  * Defines how an API node's response body is formed.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
@deprecated("Deprecated for removal. Replaced with ResponseBodySchema", "v0.1")
trait ResponseContentType 
	extends StoredModelConvertible[ResponseContentTypeData] with FromIdFactory[Int, ResponseContentType] 
		with ResponseContentTypeFactoryWrapper[ResponseContentTypeData, ResponseContentType]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this response content type in the database
	  */
	def access = AccessResponseContentType(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}