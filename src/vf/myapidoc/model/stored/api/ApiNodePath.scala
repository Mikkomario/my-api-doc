package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.api.ApiNodePathData

object ApiNodePath extends StandardStoredFactory[ApiNodePathData, ApiNodePath]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiNodePathData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node path
	  * @param id   ID of this api node path in the database
	  * @param data Wrapped api node path data
	  * @return api node path with the specified id and wrapped data
	  */
	def apply(id: Int, data: ApiNodePathData): ApiNodePath = _ApiNodePath(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node path trait
	  * @param id   ID of this api node path in the database
	  * @param data Wrapped api node path data
	  * @author Mikko Hilpinen
	  * @since 15.11.2026
	  */
	private case class _ApiNodePath(id: Int, data: ApiNodePathData) extends ApiNodePath
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override def wrap(data: ApiNodePathData) = copy(data = data)
	}
}

/**
  * Represents a api node path that has already been stored in the database. 
  * Represents a path leading to an API node. Relative to the parent node (if specified) or the 
  * API root.
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait ApiNodePath extends ApiNodePathLike[ApiNodePath]

