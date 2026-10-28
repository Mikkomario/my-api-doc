package vf.myapidoc.model.factory.api

import utopia.access.model.enumeration.Method

/**
  * Common trait for classes that implement NodeAuthLinkFactory by wrapping a NodeAuthLinkFactory 
  * instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkFactoryWrapper[A <: NodeAuthLinkFactory[A], +Repr] 
	extends NodeAuthLinkFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withAuthId(authId: Int) = mapWrapped { _.withAuthId(authId) }
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	override def withMethod(method: Method) = mapWrapped { _.withMethod(method) }
	override def withNodeId(nodeId: Int) = mapWrapped { _.withNodeId(nodeId) }
}

