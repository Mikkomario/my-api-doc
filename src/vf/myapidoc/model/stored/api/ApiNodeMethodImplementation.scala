package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.node.impl.AccessApiNodeMethodImplementation
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationFactoryWrapper
import vf.myapidoc.model.partial.api.{ApiNodeMethodImplementationData, VersionedDocElementData}

object ApiNodeMethodImplementation 
	extends StandardStoredFactory[ApiNodeMethodImplementationData, ApiNodeMethodImplementation]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiNodeMethodImplementationData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api node method implementation
	  * @param id   ID of this api node method implementation in the database
	  * @param data Wrapped api node method implementation data
	  * @return api node method implementation with the specified id and wrapped data
	  */
	def apply(id: Int, data: ApiNodeMethodImplementationData): ApiNodeMethodImplementation = 
		_ApiNodeMethodImplementation(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api node method implementation trait
	  * @param id   ID of this api node method implementation in the database
	  * @param data Wrapped api node method implementation data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _ApiNodeMethodImplementation(id: Int, data: ApiNodeMethodImplementationData) 
		extends ApiNodeMethodImplementation
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ApiNodeMethodImplementationData) = copy(data = data)
	}
}

/**
  * Represents a api node method implementation that has already been stored in the database. 
  * Describes how a specific HTTP method is implemented in an API node.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait ApiNodeMethodImplementation 
	extends ApiNodeMethodImplementationFactoryWrapper[ApiNodeMethodImplementationData, ApiNodeMethodImplementation] 
		with VersionedDocElementData 
		with StoredVersionedDocElementLike[ApiNodeMethodImplementationData, ApiNodeMethodImplementation]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this api node method implementation in the database
	  */
	def access = AccessApiNodeMethodImplementation(id)
}
