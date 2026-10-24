package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.AccessApi
import vf.myapidoc.model.factory.api.ApiFactoryWrapper
import vf.myapidoc.model.partial.api.ApiData

object Api extends StandardStoredFactory[ApiData, Api]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ApiData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new api
	  * @param id   ID of this api in the database
	  * @param data Wrapped api data
	  * @return api with the specified id and wrapped data
	  */
	def apply(id: Int, data: ApiData): Api = _Api(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the api trait
	  * @param id   ID of this api in the database
	  * @param data Wrapped api data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _Api(id: Int, data: ApiData) extends Api
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: ApiData) = copy(data = data)
	}
}

/**
  * Represents a api that has already been stored in the database. 
  * Represents a documented API
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait Api 
	extends StoredModelConvertible[ApiData] with FromIdFactory[Int, Api] with ApiFactoryWrapper[ApiData, Api]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this api in the database
	  */
	def access = AccessApi(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}
