package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import vf.myapidoc.model.factory.api.NodeAuthLinkFactory

import java.time.Instant

/**
  * Common trait for classes that provide read and copy access to node auth link properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDataLike[+Repr <: VersionedDocElementData] 
	extends HasNodeAuthLinkProps with NodeAuthLinkFactory[Repr] with VersionedDocElementDataLike[Repr] 
		with VersionedDocElementData
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this node auth link
	  * @param originalUpdateId New original update id to assign. Default = current value.
	  * @param nodeId           New node id to assign. Default = current value.
	  * @param authId           New auth id to assign. Default = current value.
	  * @param removingUpdateId New removing update id to assign. Default = current value.
	  * @param method           New method to assign. Default = current value.
	  * @param description      New description to assign. Default = current value.
	  * @return A copy of this node auth link with the specified properties
	  */
	def copyNodeAuthLink(originalUpdateId: Int = originalUpdateId, nodeId: Int = nodeId, 
		authId: Int = authId, removingUpdateId: Option[Int] = removingUpdateId, 
		method: Option[Method] = method, description: String = description): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copyNodeAuthLink(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withAuthId(authId: Int) = copyNodeAuthLink(authId = authId)
	override def withDescription(description: String) = copyNodeAuthLink(description = description)
	override def withMethod(method: Method) = copyNodeAuthLink(method = Some(method))
	override def withNodeId(nodeId: Int) = copyNodeAuthLink(nodeId = nodeId)
	override def withOriginalUpdateId(originalUpdateId: Int) = 
		copyNodeAuthLink(originalUpdateId = originalUpdateId)
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		copyNodeAuthLink(removingUpdateId = Some(removingUpdateId))
}

