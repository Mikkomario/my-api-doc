package vf.myapidoc.database.access.api.body

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}
import vf.myapidoc.database.props.api.BodySchemaDbProps

/**
  * An interface which provides body schema -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include body_schema.
  * @param model   A model used for accessing body schema database properties
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class FilterByBodySchema[+A <: FilterableView[A]](wrapped: A, model: BodySchemaDbProps) 
	extends FilterBodySchemas[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

