package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

/**
  * An interface which provides node auth link -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include node_auth_link.
  * @param model   A model used for accessing node auth link database properties
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByNodeAuthLink[+A <: FilterableView[A]](wrapped: A, model: NodeAuthLinkDbProps) 
	extends FilterNodeAuthLinks[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

