package vf.myapidoc.database.access.schema.prop.types

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides alternative property type -based filtering for other types of 
  * access points.
  * @param wrapped Wrapped access point. Expected to include alternative_property_type.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByAlternativePropertyType[+A <: FilterableView[A]](wrapped: A) 
	extends FilterAlternativePropertyTypes[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

