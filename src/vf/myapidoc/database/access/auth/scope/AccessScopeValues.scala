package vf.myapidoc.database.access.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.auth.ScopeDbModel

/**
  * Used for accessing scope values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScopeValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing scope database properties
	  */
	val model = ScopeDbModel
	
	/**
	  * Access to scope ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * ID of the API where this scope is used.
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	
	/**
	  * Name / identifier of this scope.
	  */
	lazy val names = apply(model.name) { v => v.getString }
	
	/**
	  * Time when this scope was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	
	/**
	  * Time when this scope became deprecated. None while this scope is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
}

