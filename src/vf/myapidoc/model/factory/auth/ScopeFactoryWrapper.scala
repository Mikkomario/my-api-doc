package vf.myapidoc.model.factory.auth

import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement ScopeFactory by wrapping a ScopeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ScopeFactoryWrapper[A <: ScopeFactory[A], +Repr] 
	extends ScopeFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withApiId(apiId: Int) = mapWrapped { _.withApiId(apiId) }
	override def withName(name: String) = mapWrapped { _.withName(name) }
}

