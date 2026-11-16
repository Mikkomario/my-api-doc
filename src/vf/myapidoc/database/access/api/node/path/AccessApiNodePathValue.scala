package vf.myapidoc.database.access.api.node.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.ApiNodePathDbModel

/**
  * Used for accessing individual api node path values from the DB
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class AccessApiNodePathValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node path database properties
	  */
	override val model = ApiNodePathDbModel
	
	/**
	  * ID of the API node at the end of this path
	  */
	lazy val nodeId = apply(model.nodeId).optional { v => v.int }
	
	/**
	  * This path as a string; Relative to the parent node (if defined) or the API root. 
	  * May refer to path parameters.
	  */
	lazy val relative = apply(model.relative) { v => v.getString }
	
	/**
	  * ID of the model type that describes the received path parameters. None if no path parameters 
	  * are accepted.
	  */
	lazy val paramsModelSchemaId = apply(model.paramsModelSchemaId).optional { v => v.int }
}

