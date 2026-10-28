package vf.myapidoc.database.access.api.update

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides doc update -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include doc_update.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class FilterByDocUpdate[+A <: FilterableView[A]](wrapped: A) 
	extends FilterDocUpdates[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

