package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides additional node auth scope requirement -based filtering for other 
  * types of access points.
  * @param wrapped Wrapped access point. Expected to include 
  *                additional_node_auth_scope_requirement.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByAdditionalNodeAuthScopeRequirement[+A <: FilterableView[A]](wrapped: A) 
	extends FilterAdditionalNodeAuthScopeRequirements[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

