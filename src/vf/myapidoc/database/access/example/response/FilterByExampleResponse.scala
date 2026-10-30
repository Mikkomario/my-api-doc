package vf.myapidoc.database.access.example.response

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides example response -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include example_response.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class FilterByExampleResponse[+A <: FilterableView[A]](wrapped: A) 
	extends FilterExampleResponses[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

