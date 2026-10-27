package vf.myapidoc.model.combined.api

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.model.immutable.{Constant, Model}
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.NodeAuthScopeLinkData
import vf.myapidoc.model.stored.api.{NodeAuthScopeLink, StoredNodeAuthLinkLike}
import vf.myapidoc.model.stored.auth.Scope
import vf.myapidoc.model.template.ClientModelConvertible

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
		with ClientModelConvertible
{
	override def toModel = super.toModel + Constant("scopes", scopes)
	override def toClientModel: Model = {
		val base = scopes.only match {
			case Some(scope) => Model.from("scope" -> scope.name)
			case None => Model.from("scopes" -> scopes.map { _.name })
		}
		if (data.description.isEmpty)
			base
		else
			base + Constant("description", data.description)
	}
	
	override def withId(id: Int): DetailedNodeAuthScopeLink = copy(id = id)
	override protected def wrap(factory: NodeAuthScopeLinkData): DetailedNodeAuthScopeLink = copy(data = factory)
}