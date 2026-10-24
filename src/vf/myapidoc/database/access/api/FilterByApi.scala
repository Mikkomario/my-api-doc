package vf.myapidoc.database.access.api

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides api -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include api.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class FilterByApi[+A <: FilterableView[A]](wrapped: A) 
	extends FilterApis[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

