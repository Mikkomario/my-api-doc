package vf.myapidoc.model.factory.api

import vf.myapidoc.model.enumeration.PropertyType

/**
  * Common trait for classes that implement BodySchemaFactory by wrapping a BodySchemaFactory 
  * instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaFactoryWrapper[A <: BodySchemaFactory[A], +Repr] 
	extends BodySchemaFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withDataType(dataType: PropertyType) = mapWrapped { _.withDataType(dataType) }
	override def withIsArray(isArray: Boolean) = mapWrapped { _.withIsArray(isArray) }
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	override def withParentId(parentId: Int) = mapWrapped { _.withParentId(parentId) }
}

