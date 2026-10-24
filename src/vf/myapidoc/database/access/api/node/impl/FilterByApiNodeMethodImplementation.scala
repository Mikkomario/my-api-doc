package vf.myapidoc.database.access.api.node.impl

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides api node method implementation -based filtering for other types 
  * of access points.
  * @param wrapped Wrapped access point. Expected to include api_node_method_implementation.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByApiNodeMethodImplementation[+A <: FilterableView[A]](wrapped: A) 
	extends FilterApiNodeMethodImplementations[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

