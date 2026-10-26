package vf.myapidoc.model.stored.api

import utopia.access.model.enumeration.Method
import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.factory.api.NodeAuthLinkFactoryWrapper
import vf.myapidoc.model.partial.api.NodeAuthLinkDataLike

import java.time.Instant

/**
  * Common trait for node auth links which have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait StoredNodeAuthLinkLike[Data <: NodeAuthLinkDataLike[Data], +Repr] 
	extends Stored[Data, Int] with StoredModelConvertible[Data] with FromIdFactory[Int, Repr] 
		with NodeAuthLinkFactoryWrapper[Data, Repr] with NodeAuthLinkDataLike[Repr]
{
	// IMPLEMENTED	--------------------
	
	override def authId = data.authId
	override def created = data.created
	override def deprecatedAfter = data.deprecatedAfter
	override def description = data.description
	override def method = data.method
	override def nodeId = data.nodeId
	
	override protected def wrappedFactory = data
	
	override def copyNodeAuthLink(nodeId: Int, authId: Int, method: Option[Method], description: String, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		wrap(data.copyNodeAuthLink(nodeId, authId, method, description, created, deprecatedAfter))
}

