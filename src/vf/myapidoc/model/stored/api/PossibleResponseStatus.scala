package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.response.status.AccessPossibleResponseStatus
import vf.myapidoc.model.factory.api.PossibleResponseStatusFactoryWrapper
import vf.myapidoc.model.partial.api.{PossibleResponseStatusData, VersionedDocElementData}

object PossibleResponseStatus 
	extends StandardStoredFactory[PossibleResponseStatusData, PossibleResponseStatus]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = PossibleResponseStatusData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new possible response status
	  * @param id   ID of this possible response status in the database
	  * @param data Wrapped possible response status data
	  * @return possible response status with the specified id and wrapped data
	  */
	def apply(id: Int, data: PossibleResponseStatusData): PossibleResponseStatus = 
		_PossibleResponseStatus(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the possible response status trait
	  * @param id   ID of this possible response status in the database
	  * @param data Wrapped possible response status data
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _PossibleResponseStatus(id: Int, data: PossibleResponseStatusData) 
		extends PossibleResponseStatus
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: PossibleResponseStatusData) = copy(data = data)
	}
}

/**
  * Represents a possible response status that has already been stored in the database. 
  * Defines a possible response status for an API node method call.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait PossibleResponseStatus 
	extends PossibleResponseStatusFactoryWrapper[PossibleResponseStatusData, PossibleResponseStatus] 
		with VersionedDocElementData 
		with StoredVersionedDocElementLike[PossibleResponseStatusData, PossibleResponseStatus]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this possible response status in the database
	  */
	def access = AccessPossibleResponseStatus(id)
}

