package vf.myapidoc.model.factory.schema

import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement PropertySchemaFactory by wrapping another such instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PropertySchemaFactoryWrapper[Data <: PropertySchemaFactory[Data], +Repr]
	extends PropertySchemaFactory[Repr] with VersionedDocElementFactoryWrapper[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withContentTypeId(contentTypeId: Int) = mapData { _.withContentTypeId(contentTypeId) }
	override def withDataType(dataType: PropertyType) = mapData { _.withDataType(dataType) }
	override def withDescription(description: String) = mapData { _.withDescription(description) }
	override def withIsArray(isArray: UncertainBoolean) = mapData { _.withIsArray(isArray) }
	override def withIsOptional(isOptional: Boolean) = mapData { _.withIsOptional(isOptional) }
	override def withIsSensitive(isSensitive: Boolean) = mapData { _.withIsSensitive(isSensitive) }
	override def withModelTypeId(modelTypeId: Int) = mapData { _.withModelTypeId(modelTypeId) }
	override def withName(name: String) = mapData { _.withName(name) }
}

