package vf.myapidoc.database.access.api.body

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides request body schema -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include request_body_schema.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByRequestBodySchema[+A <: FilterableView[A]](wrapped: A) 
	extends FilterRequestBodySchemas[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

