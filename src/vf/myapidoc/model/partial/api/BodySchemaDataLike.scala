package vf.myapidoc.model.partial.api

import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.BodySchemaFactory

import java.time.Instant

/**
  * Common trait for classes that provide read and copy access to body schema properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDataLike[+Repr <: VersionedDocElementData] 
	extends HasBodySchemaProps with BodySchemaFactory[Repr] with VersionedDocElementDataLike[Repr] 
		with VersionedDocElementData
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this body schema
	  * @param originalUpdateId New original update id to assign. Default = current value.
	  * @param parentId         New parent id to assign. Default = current value.
	  * @param removingUpdateId New removing update id to assign. Default = current value.
	  * @param dataType         New data type to assign. Default = current value.
	  * @param modelTypeId      New model type id to assign. Default = current value.
	  * @param isArray          New is array to assign. Default = current value.
	  * @return A copy of this body schema with the specified properties
	  */
	def copyBodySchema(originalUpdateId: Int = originalUpdateId, parentId: Int = parentId, 
		removingUpdateId: Option[Int] = removingUpdateId, dataType: PropertyType = dataType, 
		modelTypeId: Option[Int] = modelTypeId, isArray: Boolean = isArray): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copyBodySchema(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDataType(dataType: PropertyType) = copyBodySchema(dataType = dataType)
	override def withIsArray(isArray: Boolean) = copyBodySchema(isArray = isArray)
	override def withModelTypeId(modelTypeId: Int) = copyBodySchema(modelTypeId = Some(modelTypeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = copyBodySchema(originalUpdateId = originalUpdateId)
	override def withParentId(parentId: Int) = copyBodySchema(parentId = parentId)
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		copyBodySchema(removingUpdateId = Some(removingUpdateId))
}

