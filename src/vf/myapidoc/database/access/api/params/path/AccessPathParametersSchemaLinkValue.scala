package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Used for accessing individual path parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
@deprecated("Replaced with AccessApiNodePathValue", "v0.1")
case class AccessPathParametersSchemaLinkValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing path parameters schema link database properties
	  */
	override val model = PathParametersSchemaLinkDbModel
	
	/**
	  * ID of the API node path where these parameters are placed.
	  */
	lazy val pathId = apply(model.pathId).optional { v => v.int }
	/**
	  * ID of the model type that describes the received path parameters.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
}

