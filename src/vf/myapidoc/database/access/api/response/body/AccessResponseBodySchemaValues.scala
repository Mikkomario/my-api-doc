package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.body.AccessBodySchemaValues
import vf.myapidoc.database.storable.api.ResponseBodySchemaDbModel

/**
  * Used for accessing response body schema values from the DB
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class AccessResponseBodySchemaValues(access: AccessManyColumns) extends AccessBodySchemaValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing response body schema database properties
	  */
	override val model = ResponseBodySchemaDbModel
}

