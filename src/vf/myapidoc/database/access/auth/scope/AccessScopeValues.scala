package vf.myapidoc.database.access.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.auth.ScopeDbModel

/**
  * Used for accessing scope values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScopeValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing scope database properties
	  */
	override val model = ScopeDbModel
	
	/**
	  * ID of the API where this scope is used.
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	/**
	  * Name / identifier of this scope.
	  */
	lazy val names = apply(model.name) { v => v.getString }
}

