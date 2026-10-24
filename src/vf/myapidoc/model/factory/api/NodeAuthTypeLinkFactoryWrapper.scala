package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement NodeAuthTypeLinkFactory by wrapping a 
  * NodeAuthTypeLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthTypeLinkFactoryWrapper[A <: NodeAuthTypeLinkFactory[A], +Repr] 
	extends NodeAuthTypeLinkFactory[Repr] with NodeAuthLinkFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withAuthTypeId(authTypeId: Int) = withAuthId(authTypeId)
}

