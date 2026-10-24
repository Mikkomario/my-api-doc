package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Used for accessing path parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPathParametersSchemaLinkValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing path parameters schema link database properties
	  */
	val model = PathParametersSchemaLinkDbModel
	
	/**
	  * Access to path parameters schema link ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the API node that accepts these parameters.
	  */
	lazy val nodeIds = apply(model.nodeId) { v => v.getInt }
	/**
	  * ID of the model type that describes the received path parameters.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	/**
	  * Time when this path parameters schema link was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this path parameters schema link became deprecated. None while this path parameters 
	  * schema link is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	
	
	// COMPUTED ----------------------------
	
	/**
	 * Pulls accessible node-to-model links.
	 * Assumes that only 0-1 links are available for each node.
	 * @param connection Implicit DB connection
	 * @return A map where keys are API node IDs and values are model schema IDs
	 */
	def modelIdPerNodeId(implicit connection: Connection) =
		access.toMap(model.nodeId, model.modelTypeId) { _.getInt } { _.getInt }
}

