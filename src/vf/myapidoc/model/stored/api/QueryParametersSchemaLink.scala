package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.api.params.query.AccessQueryParametersSchemaLink
import vf.myapidoc.model.factory.api.QueryParametersSchemaLinkFactoryWrapper
import vf.myapidoc.model.partial.api.{ApiNodeMethodImplementationModelSchemaLinkData, QueryParametersSchemaLinkData}

object QueryParametersSchemaLink 
	extends StandardStoredFactory[QueryParametersSchemaLinkData, QueryParametersSchemaLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = QueryParametersSchemaLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new query parameters schema link
	  * @param id   ID of this query parameters schema link in the database
	  * @param data Wrapped query parameters schema link data
	  * @return query parameters schema link with the specified id and wrapped data
	  */
	def apply(id: Int, data: QueryParametersSchemaLinkData): QueryParametersSchemaLink = 
		_QueryParametersSchemaLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the query parameters schema link trait
	  * @param id   ID of this query parameters schema link in the database
	  * @param data Wrapped query parameters schema link data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _QueryParametersSchemaLink(id: Int, data: QueryParametersSchemaLinkData) 
		extends QueryParametersSchemaLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: QueryParametersSchemaLinkData) = copy(data = data)
	}
}

/**
  * Represents a query parameters schema link that has already been stored in the database. 
  * Applies a specific schema to the query parameters accepted by an API node.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait QueryParametersSchemaLink 
	extends QueryParametersSchemaLinkFactoryWrapper[QueryParametersSchemaLinkData, QueryParametersSchemaLink] 
		with ApiNodeMethodImplementationModelSchemaLinkData 
		with StoredApiNodeMethodImplementationModelSchemaLinkLike[QueryParametersSchemaLinkData, QueryParametersSchemaLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this query parameters schema link in the database
	  */
	def access = AccessQueryParametersSchemaLink(id)
}
