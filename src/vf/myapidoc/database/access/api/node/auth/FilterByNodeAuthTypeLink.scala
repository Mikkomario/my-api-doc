package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides node auth type link -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include node_auth_type_link.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByNodeAuthTypeLink[+A <: FilterableView[A]](wrapped: A) 
	extends FilterNodeAuthTypeLinks[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

