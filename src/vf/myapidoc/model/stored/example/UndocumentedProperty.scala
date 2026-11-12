package vf.myapidoc.model.stored.example

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.example.prop.undocumented.AccessUndocumentedProperty
import vf.myapidoc.model.factory.example.UndocumentedPropertyFactoryWrapper
import vf.myapidoc.model.partial.example.UndocumentedPropertyData

object UndocumentedProperty extends StandardStoredFactory[UndocumentedPropertyData, UndocumentedProperty]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = UndocumentedPropertyData
}

/**
  * Represents a undocumented property that has already been stored in the database. 
  * Documents a property received from a tested API that didn't appear in the documentation
  * @param id   ID of this undocumented property in the database
  * @param data Wrapped undocumented property data
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class UndocumentedProperty(id: Int, data: UndocumentedPropertyData) 
	extends StoredModelConvertible[UndocumentedPropertyData] with FromIdFactory[Int, UndocumentedProperty] 
		with UndocumentedPropertyFactoryWrapper[UndocumentedPropertyData, UndocumentedProperty]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this undocumented property in the database
	  */
	def access = AccessUndocumentedProperty(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override def wrap(data: UndocumentedPropertyData) = copy(data = data)
}

