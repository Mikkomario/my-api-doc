package vf.myapidoc.model.combined.api

import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.NodeAuthScopeLinkData
import vf.myapidoc.model.stored.api.{NodeAuthScopeLink, StoredNodeAuthLinkLike}
import vf.myapidoc.model.stored.auth.Scope

object DetailedNodeAuthScopeLink
{
	def apply(link: NodeAuthScopeLink, scopes: Seq[Scope]): DetailedNodeAuthScopeLink =
		DetailedNodeAuthScopeLink(link.id, link.data, scopes)
}

/**
 * Includes full scope information with a NodeAuthScopeLink
 * @param id ID of this scope link
 * @param data This scope link's data
 * @param scopes Linked scopes, including other required scopes
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedNodeAuthScopeLink(id: Int, data: NodeAuthScopeLinkData, scopes: Seq[Scope])
	extends NodeAuthScopeLink with StoredNodeAuthLinkLike[NodeAuthScopeLinkData, DetailedNodeAuthScopeLink]
		with NodeAuthScopeLinkFactoryWrapper[NodeAuthScopeLinkData, DetailedNodeAuthScopeLink]
{
	override def withId(id: Int): DetailedNodeAuthScopeLink = copy(id = id)
	override protected def wrap(factory: NodeAuthScopeLinkData): DetailedNodeAuthScopeLink = copy(data = factory)
}