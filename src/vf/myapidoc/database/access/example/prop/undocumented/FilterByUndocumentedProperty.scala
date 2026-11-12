package vf.myapidoc.database.access.example.prop.undocumented

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides undocumented property -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include undocumented_property.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class FilterByUndocumentedProperty[+A <: FilterableView[A]](wrapped: A) 
	extends FilterUndocumentedProperties[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

