package vf.myapidoc.database.access.schema.model

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides model schema -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include model_schema.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByModelSchema[+A <: FilterableView[A]](wrapped: A) 
	extends FilterModelSchemas[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

