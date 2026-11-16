package vf.myapidoc.database.access.api.node.path

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides api node path -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include api_node_path.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class FilterByApiNodePath[+A <: FilterableView[A]](wrapped: A) 
	extends FilterApiNodePaths[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

