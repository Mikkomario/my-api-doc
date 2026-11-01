package vf.myapidoc.database.access.test.file

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface that provides tests file -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include tests_file.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class FilterByTestsFile[+A <: FilterableView[A]](wrapped: A) 
	extends FilterTestFiles[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

