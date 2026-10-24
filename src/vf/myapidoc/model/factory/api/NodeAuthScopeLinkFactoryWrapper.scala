package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement NodeAuthScopeLinkFactory by wrapping a 
  * NodeAuthScopeLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthScopeLinkFactoryWrapper[A <: NodeAuthScopeLinkFactory[A], +Repr] 
	extends NodeAuthScopeLinkFactory[Repr] with NodeAuthLinkFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withScopeId(scopeId: Int) = withAuthId(scopeId)
}

