package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import vf.myapidoc.model.factory.api.NodeAuthLinkFactory

import java.time.Instant

/**
  * Common trait for classes which provide read and copy access to node auth link properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDataLike[+Repr] extends HasNodeAuthLinkProps with NodeAuthLinkFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this node auth link
	  * @param nodeId          New node id to assign. Default = current value.
	  * @param authId          New auth id to assign. Default = current value.
	  * @param method          New method to assign. Default = current value.
	  * @param description     New description to assign. Default = current value.
	  * @param created         New created to assign. Default = current value.
	  * @param deprecatedAfter New deprecated after to assign. Default = current value.
	  * @return A copy of this node auth link with the specified properties
	  */
	def copyNodeAuthLink(nodeId: Int = nodeId, authId: Int = authId, method: Option[Method] = method, 
		description: String = description, created: Instant = created, 
		deprecatedAfter: Option[Instant] = deprecatedAfter): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withAuthId(authId: Int) = copyNodeAuthLink(authId = authId)
	
	override def withCreated(created: Instant) = copyNodeAuthLink(created = created)
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyNodeAuthLink(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copyNodeAuthLink(description = description)
	
	override def withMethod(method: Method) = copyNodeAuthLink(method = Some(method))
	
	override def withNodeId(nodeId: Int) = copyNodeAuthLink(nodeId = nodeId)
}

