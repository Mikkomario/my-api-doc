package vf.myapidoc.database.access.api.node.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.AdditionalNodeAuthScopeRequirementDbModel

/**
  * Used for accessing additional node auth scope requirement values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessAdditionalNodeAuthScopeRequirementValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing additional node auth scope requirement database properties
	  */
	val model = AdditionalNodeAuthScopeRequirementDbModel
	
	/**
	  * Access to additional node auth scope requirement ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the auth-scope link that requires the additional scope
	  */
	lazy val scopeLinkIds = apply(model.scopeLinkId) { v => v.getInt }
	/**
	  * ID of the additional authorization scope required by the node.
	  */
	lazy val scopeIds = apply(model.scopeId) { v => v.getInt }
	
	
	// COMPUTED -----------------------
	
	/**
	 * @param connection Implicit DB connection
	 * @return A map where keys are scope link IDs and values are additional required scope IDs (many-to-one)
	 */
	def scopeIdsPerLinkId(implicit connection: Connection) =
		access.toMultiMap(model.scopeLinkId, model.scopeId, includeDefault = true) { _.getInt } { _.map { _.getInt } }
}

