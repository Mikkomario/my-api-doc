package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides response content type -based filtering for other types of access 
  * points.
  * @param wrapped Wrapped access point. Expected to include response_content_type.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByResponseContentType[+A <: FilterableView[A]](wrapped: A) 
	extends FilterResponseContentTypes[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

