package vf.myapidoc.model.stored.api

import utopia.access.model.enumeration.Method
import utopia.flow.util.Mutate
import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.factory.api.{NodeAuthLinkFactory, NodeAuthLinkFactoryWrapper}
import vf.myapidoc.model.partial.api.{NodeAuthLinkDataLike, VersionedDocElementData}

/**
  * Common trait for node auth links that have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait StoredNodeAuthLinkLike[Data <: NodeAuthLinkDataLike[Data], +Repr <: VersionedDocElementData]
	extends NodeAuthLinkFactory[Repr] with NodeAuthLinkDataLike[Repr] 
		with StoredVersionedDocElementLike[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def authId = data.authId
	override def description = data.description
	override def method = data.method
	override def nodeId = data.nodeId
	
	override def copyNodeAuthLink(originalUpdateId: Int, nodeId: Int, authId: Int, removingUpdateId: Option[Int],
	                              method: Option[Method], description: String) =
		wrap(data.copyNodeAuthLink(originalUpdateId, nodeId, authId, removingUpdateId, method, description))
	
	override protected def mapWrapped(f: Mutate[Data]) = wrap(f(data))
}

