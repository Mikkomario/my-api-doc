package vf.myapidoc.model.factory.api

import utopia.access.model.enumeration.Method

/**
  * Common trait for classes that implement ApiNodeMethodImplementationFactory by wrapping a 
  * ApiNodeMethodImplementationFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationFactoryWrapper[A <: ApiNodeMethodImplementationFactory[A], +Repr] 
	extends ApiNodeMethodImplementationFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	override def withMethod(method: Method) = mapWrapped { _.withMethod(method) }
	override def withNodeId(nodeId: Int) = mapWrapped { _.withNodeId(nodeId) }
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		mapWrapped { _.withRequiresRequestBody(requiresRequestBody) }
}

