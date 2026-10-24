package vf.myapidoc.database.access.auth.token

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}

/**
  * An interface which provides auth token type -based filtering for other types of access points.
  * @param wrapped Wrapped access point. Expected to include auth_token_type.
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class FilterByAuthTokenType[+A <: FilterableView[A]](wrapped: A) 
	extends FilterAuthTokenTypes[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

