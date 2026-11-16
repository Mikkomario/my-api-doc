package vf.myapidoc.database.access.api.node.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.ApiNodePathDbModel

/**
  * Used for accessing api node path values from the DB
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class AccessApiNodePathValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node path database properties
	  */
	override val model = ApiNodePathDbModel
	
	/**
	  * ID of the API node at the end of this path
	  */
	lazy val nodeIds = apply(model.nodeId) { v => v.getInt }
	
	/**
	  * This path as a string; Relative to the parent node (if defined) or the API root. 
	  * May refer to path parameters.
	  */
	lazy val relatives = apply(model.relative) { v => v.getString }
	
	/**
	  * ID of the model type that describes the received path parameters. None if no path parameters 
	  * are accepted.
	  */
	lazy val paramsModelSchemaIds = apply(model.paramsModelSchemaId).flatten { v => v.int }
}

