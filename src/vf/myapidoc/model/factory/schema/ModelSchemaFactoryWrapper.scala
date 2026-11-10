package vf.myapidoc.model.factory.schema

import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement ModelSchemaFactory by wrapping a ModelSchemaFactory 
  * instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ModelSchemaFactoryWrapper[A <: ModelSchemaFactory[A], +Repr] 
	extends ModelSchemaFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withApiId(apiId: Int) = mapData { _.withApiId(apiId) }
	override def withDescription(description: String) = mapData { _.withDescription(description) }
	override def withName(name: String) = mapData { _.withName(name) }
}

