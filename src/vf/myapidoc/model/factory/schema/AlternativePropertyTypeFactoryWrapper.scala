package vf.myapidoc.model.factory.schema

import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement AlternativePropertyTypeFactory by wrapping a 
  * AlternativePropertyTypeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait AlternativePropertyTypeFactoryWrapper[A <: AlternativePropertyTypeFactory[A], +Repr] 
	extends AlternativePropertyTypeFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withAltType(altType: PropertyType) = mapWrapped { _.withAltType(altType) }
	override def withPropId(propId: Int) = mapWrapped { _.withPropId(propId) }
}

