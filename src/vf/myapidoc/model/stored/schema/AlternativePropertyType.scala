package vf.myapidoc.model.stored.schema

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.schema.prop.types.AccessAlternativePropertyType
import vf.myapidoc.model.factory.schema.AlternativePropertyTypeFactoryWrapper
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.partial.schema.AlternativePropertyTypeData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike

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
	extends AlternativePropertyTypeFactoryWrapper[AlternativePropertyTypeData, AlternativePropertyType] 
		with VersionedDocElementData 
		with StoredVersionedDocElementLike[AlternativePropertyTypeData, AlternativePropertyType]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this alternative property type in the database
	  */
	def access = AccessAlternativePropertyType(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	override protected def wrap(data: AlternativePropertyTypeData) = copy(data = data)
}

