package vf.myapidoc.model.factory.schema

import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement PossiblePropertyValueFactory by wrapping a 
  * PossiblePropertyValueFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
trait PossiblePropertyValueFactoryWrapper[A <: PossiblePropertyValueFactory[A], +Repr] 
	extends PossiblePropertyValueFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	override def withPropId(propId: Int) = mapWrapped { _.withPropId(propId) }
	override def withValue(value: String) = mapWrapped { _.withValue(value) }
}

