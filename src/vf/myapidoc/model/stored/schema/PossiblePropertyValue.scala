package vf.myapidoc.model.stored.schema

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.schema.prop.value.AccessPossiblePropertyValue
import vf.myapidoc.model.factory.schema.PossiblePropertyValueFactoryWrapper
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.partial.schema.PossiblePropertyValueData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike

object PossiblePropertyValue extends StandardStoredFactory[PossiblePropertyValueData, PossiblePropertyValue]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = PossiblePropertyValueData
}

/**
  * Represents a possible property value that has already been stored in the database. 
  * Defines a possible value for an enum property
  * @param id   ID of this possible property value in the database
  * @param data Wrapped possible property value data
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class PossiblePropertyValue(id: Int, data: PossiblePropertyValueData) 
	extends PossiblePropertyValueFactoryWrapper[PossiblePropertyValueData, PossiblePropertyValue] 
		with VersionedDocElementData 
		with StoredVersionedDocElementLike[PossiblePropertyValueData, PossiblePropertyValue]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this possible property value in the database
	  */
	def access = AccessPossiblePropertyValue(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: PossiblePropertyValueData) = copy(data = data)
}

