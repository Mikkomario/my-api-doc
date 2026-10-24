package vf.myapidoc.database.access.api.node.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.AdditionalNodeAuthScopeRequirementDbModel

/**
  * Used for accessing individual additional node auth scope requirement values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessAdditionalNodeAuthScopeRequirementValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing additional node auth scope requirement database properties
	  */
	val model = AdditionalNodeAuthScopeRequirementDbModel
	
	/**
	  * Access to additional node auth scope requirement id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the auth-scope link that requires the additional scope
	  */
	lazy val scopeLinkId = apply(model.scopeLinkId).optional { v => v.int }
	
	/**
	  * ID of the additional authorization scope required by the node.
	  */
	lazy val scopeId = apply(model.scopeId).optional { v => v.int }
}

