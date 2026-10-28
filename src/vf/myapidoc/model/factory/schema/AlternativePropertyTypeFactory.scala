package vf.myapidoc.model.factory.schema

import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.VersionedDocElementFactory

/**
  * Common trait for alternative property type-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait AlternativePropertyTypeFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param altType New alt type to assign
	  * @return Copy of this item with the specified alt type
	  */
	def withAltType(altType: PropertyType): A
	/**
	  * @param propId New prop id to assign
	  * @return Copy of this item with the specified prop id
	  */
	def withPropId(propId: Int): A
}

