package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.api.NodeAuthLinkData

object NodeAuthLink extends StandardStoredFactory[NodeAuthLinkData, NodeAuthLink]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = NodeAuthLinkData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new node auth link
	  * @param id   ID of this node auth link in the database
	  * @param data Wrapped node auth link data
	  * @return node auth link with the specified id and wrapped data
	  */
	def apply(id: Int, data: NodeAuthLinkData): NodeAuthLink = _NodeAuthLink(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the node auth link trait
	  * @param id   ID of this node auth link in the database
	  * @param data Wrapped node auth link data
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _NodeAuthLink(id: Int, data: NodeAuthLinkData) extends NodeAuthLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: NodeAuthLinkData) = copy(data = data)
	}
}

/**
  * Represents a node auth link that has already been stored in the database. 
  * Common trait for links between API nodes and possible authorization requirements
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLink extends StoredNodeAuthLinkLike[NodeAuthLinkData, NodeAuthLink] with NodeAuthLinkData

