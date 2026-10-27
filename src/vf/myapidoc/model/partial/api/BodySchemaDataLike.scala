package vf.myapidoc.model.partial.api

import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.BodySchemaFactory

import java.time.Instant

/**
  * Common trait for classes which provide read and copy access to body schema properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDataLike[+Repr] extends HasBodySchemaProps with BodySchemaFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this body schema
	  * @param parentId        New parent id to assign. Default = current value.
	  * @param dataType        New data type to assign. Default = current value.
	  * @param modelTypeId     New model type id to assign. Default = current value.
	  * @param created         New created to assign. Default = current value.
	  * @param deprecatedAfter New deprecated after to assign. Default = current value.
	  * @param isArray         New is array to assign. Default = current value.
	  * @return A copy of this body schema with the specified properties
	  */
	def copyBodySchema(parentId: Int = parentId, dataType: PropertyType = dataType, 
		modelTypeId: Option[Int] = modelTypeId, created: Instant = created, 
		deprecatedAfter: Option[Instant] = deprecatedAfter, isArray: Boolean = isArray): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withCreated(created: Instant) = copyBodySchema(created = created)
	
	override def withDataType(dataType: PropertyType) = copyBodySchema(dataType = dataType)
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyBodySchema(deprecatedAfter = Some(deprecatedAfter))
	
	override def withIsArray(isArray: Boolean) = copyBodySchema(isArray = isArray)
	
	override def withModelTypeId(modelTypeId: Int) = copyBodySchema(modelTypeId = Some(modelTypeId))
	
	override def withParentId(parentId: Int) = copyBodySchema(parentId = parentId)
}

