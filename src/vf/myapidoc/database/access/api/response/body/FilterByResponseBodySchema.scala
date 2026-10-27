package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides response body schema -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include response_body_schema.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class FilterByResponseBodySchema[+A <: FilterableView[A]](wrapped: A) 
	extends FilterResponseBodySchemas[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

