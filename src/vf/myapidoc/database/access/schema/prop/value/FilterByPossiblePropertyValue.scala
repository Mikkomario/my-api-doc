package vf.myapidoc.database.access.schema.prop.value

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides possible property value -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include possible_property_value.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class FilterByPossiblePropertyValue[+A <: FilterableView[A]](wrapped: A) 
	extends FilterPossiblePropertyValues[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

