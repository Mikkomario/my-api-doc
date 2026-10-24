package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Used for accessing individual path parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPathParametersSchemaLinkValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing path parameters schema link database properties
	  */
	val model = PathParametersSchemaLinkDbModel
	
	/**
	  * Access to path parameters schema link id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the API node that accepts these parameters.
	  */
	lazy val nodeId = apply(model.nodeId).optional { v => v.int }
	
	/**
	  * ID of the model type that describes the received path parameters.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	
	/**
	  * Time when this path parameters schema link was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this path parameters schema link became deprecated. None while this path parameters 
	  * schema link is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

