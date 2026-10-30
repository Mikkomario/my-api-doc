package vf.myapidoc.model.stored.example

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.example.response.AccessExampleResponse
import vf.myapidoc.model.factory.example.ExampleResponseFactoryWrapper
import vf.myapidoc.model.partial.example.ExampleResponseData

object ExampleResponse extends StandardStoredFactory[ExampleResponseData, ExampleResponse]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ExampleResponseData
}

/**
  * Represents a example response that has already been stored in the database. 
  * Represents a response received from a tested API
  * @param id   ID of this example response in the database
  * @param data Wrapped example response data
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExampleResponse(id: Int, data: ExampleResponseData) 
	extends StoredModelConvertible[ExampleResponseData] with FromIdFactory[Int, ExampleResponse] 
		with ExampleResponseFactoryWrapper[ExampleResponseData, ExampleResponse]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this example response in the database
	  */
	def access = AccessExampleResponse(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory: ExampleResponseData = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: ExampleResponseData) = copy(data = data)
}

