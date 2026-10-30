package vf.myapidoc.model.stored.example

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.example.prop.AccessExamplePropertyValue
import vf.myapidoc.model.factory.example.ExamplePropertyValueFactoryWrapper
import vf.myapidoc.model.partial.example.ExamplePropertyValueData

object ExamplePropertyValue extends StandardStoredFactory[ExamplePropertyValueData, ExamplePropertyValue]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = ExamplePropertyValueData
}

/**
  * Represents a example property value that has already been stored in the database. 
  * Documents a single property value received from a tested API
  * @param id   ID of this example property value in the database
  * @param data Wrapped example property value data
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExamplePropertyValue(id: Int, data: ExamplePropertyValueData) 
	extends StoredModelConvertible[ExamplePropertyValueData] with FromIdFactory[Int, ExamplePropertyValue] 
		with ExamplePropertyValueFactoryWrapper[ExamplePropertyValueData, ExamplePropertyValue]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this example property value in the database
	  */
	def access = AccessExamplePropertyValue(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory: ExamplePropertyValueData = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: ExamplePropertyValueData) = copy(data = data)
}

