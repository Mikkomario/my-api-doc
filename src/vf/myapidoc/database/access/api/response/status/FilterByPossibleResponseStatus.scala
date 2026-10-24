package vf.myapidoc.database.access.api.response.status

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides possible response status -based filtering for other types of 
  * access points.
  * @param wrapped Wrapped access point. Expected to include possible_response_status.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByPossibleResponseStatus[+A <: FilterableView[A]](wrapped: A) 
	extends FilterPossibleResponseStatuses[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

