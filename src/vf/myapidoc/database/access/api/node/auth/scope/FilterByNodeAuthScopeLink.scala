package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides node auth scope link -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include node_auth_scope_link.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByNodeAuthScopeLink[+A <: FilterableView[A]](wrapped: A) 
	extends FilterNodeAuthScopeLinks[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

