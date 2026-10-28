package vf.myapidoc.database.access.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.auth.ScopeDbModel

/**
  * Used for accessing individual scope values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScopeValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing scope database properties
	  */
	override val model = ScopeDbModel
	
	/**
	  * ID of the API where this scope is used.
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	/**
	  * Name / identifier of this scope.
	  */
	lazy val name = apply(model.name) { v => v.getString }
}

