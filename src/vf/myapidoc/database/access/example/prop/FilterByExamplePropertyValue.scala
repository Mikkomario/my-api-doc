package vf.myapidoc.database.access.example.prop

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides example property value -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include example_property_value.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class FilterByExamplePropertyValue[+A <: FilterableView[A]](wrapped: A) 
	extends FilterExamplePropertyValues[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

