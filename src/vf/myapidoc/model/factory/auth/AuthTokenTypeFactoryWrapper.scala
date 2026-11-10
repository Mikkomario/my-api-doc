package vf.myapidoc.model.factory.auth

import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.factory.api.VersionedDocElementFactoryWrapper

/**
  * Common trait for classes that implement AuthTokenTypeFactory by wrapping a 
  * AuthTokenTypeFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait AuthTokenTypeFactoryWrapper[A <: AuthTokenTypeFactory[A], +Repr] 
	extends AuthTokenTypeFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withApiId(apiId: Int) = mapData { _.withApiId(apiId) }
	override def withName(name: String) = mapData { _.withName(name) }
	override def withNameNoSpaces(nameNoSpaces: String) = mapData { _.withNameNoSpaces(nameNoSpaces) }
	override def withUseType(useType: TokenUseType) = mapData { _.withUseType(useType) }
}

