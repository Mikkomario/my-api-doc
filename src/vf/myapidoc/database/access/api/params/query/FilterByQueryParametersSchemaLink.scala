package vf.myapidoc.database.access.api.params.query

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides query parameters schema link -based filtering for other types of 
  * access points.
  * @param wrapped Wrapped access point. Expected to include query_parameters_schema_link.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByQueryParametersSchemaLink[+A <: FilterableView[A]](wrapped: A) 
	extends FilterQueryParameterSchemaLinks[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

