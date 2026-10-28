package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.api.node.auth.AccessNodeAuthTypeLink
import vf.myapidoc.model.combined.api.DetailedNodeAuthTypeLink
import vf.myapidoc.model.factory.api.NodeAuthTypeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.{NodeAuthLinkData, NodeAuthTypeLinkData}
import vf.myapidoc.model.stored.auth.AuthTokenType

object NodeAuthTypeLink extends StandardStoredFactory[NodeAuthTypeLinkData, NodeAuthTypeLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = NodeAuthTypeLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new node auth type link
	  * @param id   ID of this node auth type link in the database
	  * @param data Wrapped node auth type link data
	  * @return node auth type link with the specified id and wrapped data
	  */
	def apply(id: Int, data: NodeAuthTypeLinkData): NodeAuthTypeLink = _NodeAuthTypeLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the node auth type link trait
	  * @param id   ID of this node auth type link in the database
	  * @param data Wrapped node auth type link data
	  * @author Mikko Hilpinen
	  * @since 23.10.2026
	  */
	private case class _NodeAuthTypeLink(id: Int, data: NodeAuthTypeLinkData) extends NodeAuthTypeLink
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: NodeAuthTypeLinkData) = copy(data = data)
	}
}

/**
  * Represents a node auth type link that has already been stored in the database. 
  * Describes an authorization type accepted by an API node.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait NodeAuthTypeLink 
	extends NodeAuthTypeLinkFactoryWrapper[NodeAuthTypeLinkData, NodeAuthTypeLink] with NodeAuthLinkData 
		with StoredNodeAuthLinkLike[NodeAuthTypeLinkData, NodeAuthTypeLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this node auth type link in the database
	  */
	def access = AccessNodeAuthTypeLink(id)
	
	
	// OTHER	--------------------
	
	def +(tokenType: AuthTokenType) = DetailedNodeAuthTypeLink(this, tokenType)
}
