package vf.myapidoc.database.access.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.auth.ScopeDbModel

/**
  * Used for accessing individual scope values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScopeValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing scope database properties
	  */
	val model = ScopeDbModel
	
	/**
	  * Access to scope id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the API where this scope is used.
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	
	/**
	  * Name / identifier of this scope.
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * Time when this scope was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this scope became deprecated. None while this scope is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

