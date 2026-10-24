package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationModelSchemaLinkData

object ApiNodeMethodImplementationModelSchemaLink 
	extends StandardStoredFactory[ApiNodeMethodImplementationModelSchemaLinkData, 
		ApiNodeMethodImplementationModelSchemaLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiNodeMethodImplementationModelSchemaLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node method implementation model schema link
	  * @param id   ID of this api node method implementation model schema link in the database
	  * @param data Wrapped api node method implementation model schema link data
	  * @return api node method implementation model schema link with the specified id and wrapped data
	  */
	def apply(id: Int, 
		data: ApiNodeMethodImplementationModelSchemaLinkData): ApiNodeMethodImplementationModelSchemaLink = 
		_ApiNodeMethodImplementationModelSchemaLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node method implementation model schema link trait
	  * @param id   ID of this api node method implementation model schema link in the database
	  * @param data Wrapped api node method implementation model schema link data
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _ApiNodeMethodImplementationModelSchemaLink(id: Int, 
		data: ApiNodeMethodImplementationModelSchemaLinkData) 
		extends ApiNodeMethodImplementationModelSchemaLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ApiNodeMethodImplementationModelSchemaLinkData) = copy(data = data)
	}
}

/**
  * Represents a api node method implementation model schema link that has already been stored in 
  * the database. 
  * Links a model schema to to an API node's method implementation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLink 
	extends StoredApiNodeMethodImplementationModelSchemaLinkLike[ApiNodeMethodImplementationModelSchemaLinkData, ApiNodeMethodImplementationModelSchemaLink] 
		with ApiNodeMethodImplementationModelSchemaLinkData

