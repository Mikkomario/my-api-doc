package vf.myapidoc.database.access.api.update

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}
import vf.myapidoc.database.props.api.VersionedDocElementDbProps

/**
  * An interface that provides versioned doc element -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include versioned_doc_element.
  * @param model   A model used for accessing versioned doc element database properties
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class FilterByVersionedDocElement[+A <: FilterableView[A]](wrapped: A, 
	model: VersionedDocElementDbProps) 
	extends FilterVersionedDocElements[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

