package vf.myapidoc.database.access.api.node

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides api node -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include api_node.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByApiNode[+A <: FilterableView[A]](wrapped: A) 
	extends FilterApiNodes[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

