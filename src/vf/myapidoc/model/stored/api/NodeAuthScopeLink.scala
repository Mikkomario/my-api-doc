package vf.myapidoc.model.stored.api

import utopia.flow.collection.immutable.OptimizedIndexedSeq
import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.database.access.api.node.auth.scope.AccessNodeAuthScopeLink
import vf.myapidoc.model.combined.api.NodeAuthScopeLinkWithAdditionalRequirements
import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.{NodeAuthLinkData, NodeAuthScopeLinkData}

object NodeAuthScopeLink extends StandardStoredFactory[NodeAuthScopeLinkData, NodeAuthScopeLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = NodeAuthScopeLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new node auth scope link
	  * @param id   ID of this node auth scope link in the database
	  * @param data Wrapped node auth scope link data
	  * @return node auth scope link with the specified id and wrapped data
	  */
	def apply(id: Int, data: NodeAuthScopeLinkData): NodeAuthScopeLink = _NodeAuthScopeLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the node auth scope link trait
	  * @param id   ID of this node auth scope link in the database
	  * @param data Wrapped node auth scope link data
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _NodeAuthScopeLink(id: Int, data: NodeAuthScopeLinkData) extends NodeAuthScopeLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: NodeAuthScopeLinkData) = copy(data = data)
	}
}

/**
  * Represents a node auth scope link that has already been stored in the database. 
  * Describes an authorization scope required by an API node. 
  * If multiple scopes are linked, they typically represent alternative auth scopes, usually for 
  * different purposes.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthScopeLink 
	extends NodeAuthScopeLinkFactoryWrapper[NodeAuthScopeLinkData, NodeAuthScopeLink] with NodeAuthLinkData 
		with StoredNodeAuthLinkLike[NodeAuthScopeLinkData, NodeAuthScopeLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this node auth scope link in the database
	  */
	def access = AccessNodeAuthScopeLink(id)
	
	
	// OTHER    --------------------
	
	def withAdditionalRequirements(requirements: IterableOnce[AdditionalNodeAuthScopeRequirement]) =
		NodeAuthScopeLinkWithAdditionalRequirements(this, OptimizedIndexedSeq.from(requirements))
}

