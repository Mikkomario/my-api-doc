package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Used for accessing path parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
@deprecated("Replaced with AccessApiNodePathValues", "v0.1")
case class AccessPathParametersSchemaLinkValues(access: AccessManyColumns) 
	extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing path parameters schema link database properties
	  */
	override val model = PathParametersSchemaLinkDbModel
	
	/**
	  * ID of the API node path where these parameters are placed.
	  */
	lazy val pathIds = apply(model.pathId) { v => v.getInt }
	/**
	  * ID of the model type that describes the received path parameters.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	
	
	// COMPUTED	--------------------
	
	/**
	  * Pulls accessible path-to-model links.
	  * Assumes that only 0-1 links are available for each path.
	  * @param connection Implicit DB connection
	  */
	def modelIdPerPathId(implicit connection: Connection) =
		access.toMap(model.pathId, model.modelTypeId) { _.getInt } { _.getInt }
}

