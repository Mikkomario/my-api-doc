package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.params.path.AccessPathParametersSchemaLink
import vf.myapidoc.model.factory.api.PathParametersSchemaLinkFactoryWrapper
import vf.myapidoc.model.partial.api.PathParametersSchemaLinkData

object PathParametersSchemaLink 
	extends StandardStoredFactory[PathParametersSchemaLinkData, PathParametersSchemaLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = PathParametersSchemaLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new path parameters schema link
	  * @param id   ID of this path parameters schema link in the database
	  * @param data Wrapped path parameters schema link data
	  * @return path parameters schema link with the specified id and wrapped data
	  */
	def apply(id: Int, data: PathParametersSchemaLinkData): PathParametersSchemaLink = 
		_PathParametersSchemaLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the path parameters schema link trait
	  * @param id   ID of this path parameters schema link in the database
	  * @param data Wrapped path parameters schema link data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _PathParametersSchemaLink(id: Int, data: PathParametersSchemaLinkData) 
		extends PathParametersSchemaLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: PathParametersSchemaLinkData) = copy(data = data)
	}
}

/**
  * Represents a path parameters schema link that has already been stored in the database. 
  * Applies a specific schema to the path parameters received by an API node.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait PathParametersSchemaLink 
	extends StoredModelConvertible[PathParametersSchemaLinkData] 
		with FromIdFactory[Int, PathParametersSchemaLink] 
		with PathParametersSchemaLinkFactoryWrapper[PathParametersSchemaLinkData, PathParametersSchemaLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this path parameters schema link in the database
	  */
	def access = AccessPathParametersSchemaLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}