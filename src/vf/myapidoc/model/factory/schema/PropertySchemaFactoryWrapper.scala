package vf.myapidoc.model.factory.schema

import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement PropertySchemaFactory by wrapping a 
  * PropertySchemaFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PropertySchemaFactoryWrapper[A <: PropertySchemaFactory[A], +Repr] 
	extends PropertySchemaFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withContentTypeId(contentTypeId: Int) = mapWrapped { _.withContentTypeId(contentTypeId) }
	override def withDataType(dataType: PropertyType) = mapWrapped { _.withDataType(dataType) }
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	override def withIsArray(isArray: UncertainBoolean) = mapWrapped { _.withIsArray(isArray) }
	override def withIsOptional(isOptional: Boolean) = mapWrapped { _.withIsOptional(isOptional) }
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	override def withName(name: String) = mapWrapped { _.withName(name) }
}

