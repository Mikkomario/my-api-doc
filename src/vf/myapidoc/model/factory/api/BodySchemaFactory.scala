package vf.myapidoc.model.factory.api

import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for body schema-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param dataType New data type to assign
	  * @return Copy of this item with the specified data type
	  */
	def withDataType(dataType: PropertyType): A
	/**
	  * @param isArray New is array to assign
	  * @return Copy of this item with the specified is array
	  */
	def withIsArray(isArray: Boolean): A
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	/**
	  * @param parentId New parent id to assign
	  * @return Copy of this item with the specified parent id
	  */
	def withParentId(parentId: Int): A
}

