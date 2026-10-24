package vf.myapidoc.database.access.schema.prop

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides property schema -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include property_schema.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByPropertySchema[+A <: FilterableView[A]](wrapped: A) 
	extends FilterPropertySchemas[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

