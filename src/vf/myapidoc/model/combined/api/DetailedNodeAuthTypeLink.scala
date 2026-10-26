package vf.myapidoc.model.combined.api

import utopia.flow.generic.model.immutable.Constant
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.model.factory.api.NodeAuthTypeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.NodeAuthTypeLinkData
import vf.myapidoc.model.stored.api.{NodeAuthTypeLink, StoredNodeAuthLinkLike}
import vf.myapidoc.model.stored.auth.AuthTokenType

object DetailedNodeAuthTypeLink
{
	// OTHER	--------------------
	
	/**
	  * @param link  The link to wrap
	  * @param token token to attach
	  * @return Combination of the specified link and token
	  */
	def apply(link: NodeAuthTypeLink, token: AuthTokenType): DetailedNodeAuthTypeLink = 
		apply(link.id, link.data, token)
	
	/**
	  * @param id    ID of this link in the DB
	  * @param data  The wrapped link data
	  * @param token token to attach
	  * @return A new link with the specified token included
	  */
	def apply(id: Int, data: NodeAuthTypeLinkData, token: AuthTokenType): DetailedNodeAuthTypeLink = 
		_DetailedNodeAuthTypeLink(id, data, token)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id    ID of this link in the DB
	  * @param data  The wrapped link data
	  * @param token token to attach
	  */
	private case class _DetailedNodeAuthTypeLink(id: Int, data: NodeAuthTypeLinkData, token: AuthTokenType) 
		extends DetailedNodeAuthTypeLink
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: NodeAuthTypeLinkData) = copy(data = factory)
	}
}

/**
  * Includes the authorization type accepted by the linked node.
  * @author Mikko Hilpinen
  * @since 23.10.2026, v0.1
  */
trait DetailedNodeAuthTypeLink 
	extends NodeAuthTypeLink 
		with NodeAuthTypeLinkFactoryWrapper[NodeAuthTypeLinkData, DetailedNodeAuthTypeLink] 
		with StoredNodeAuthLinkLike[NodeAuthTypeLinkData, DetailedNodeAuthTypeLink]
{
	// ABSTRACT	--------------------
	
	/**
	  * The token that is attached to this link
	  */
	def token: AuthTokenType
	
	
	// IMPLEMENTED	--------------------

	override def toModel = super.toModel + Constant("token", token)
}

