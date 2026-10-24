package vf.myapidoc.model.stored.schema

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.schema.prop.types.AccessAlternativePropertyType
import vf.myapidoc.model.factory.schema.AlternativePropertyTypeFactoryWrapper
import vf.myapidoc.model.partial.schema.AlternativePropertyTypeData

object AlternativePropertyType 
	extends StandardStoredFactory[AlternativePropertyTypeData, AlternativePropertyType]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = AlternativePropertyTypeData
}

/**
  * Represents a alternative property type that has already been stored in the database. 
  * Defines an alternative supported data type for a property.
  * @param id   ID of this alternative property type in the database
  * @param data Wrapped alternative property type data
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AlternativePropertyType(id: Int, data: AlternativePropertyTypeData) 
	extends StoredModelConvertible[AlternativePropertyTypeData] 
		with FromIdFactory[Int, AlternativePropertyType] 
		with AlternativePropertyTypeFactoryWrapper[AlternativePropertyTypeData, AlternativePropertyType]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this alternative property type in the database
	  */
	def access = AccessAlternativePropertyType(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AlternativePropertyTypeData) = copy(data = data)
}

