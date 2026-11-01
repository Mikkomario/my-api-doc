package vf.myapidoc.database.access.test.url

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides test url -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include test_url.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class FilterByTestUrl[+A <: FilterableView[A]](wrapped: A) 
	extends FilterTestUrls[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

