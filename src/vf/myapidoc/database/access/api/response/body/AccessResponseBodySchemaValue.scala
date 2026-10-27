package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.access.api.body.AccessBodySchemaValue
import vf.myapidoc.database.storable.api.ResponseBodySchemaDbModel

/**
  * Used for accessing individual response body schema values from the DB
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class AccessResponseBodySchemaValue(access: AccessColumn) extends AccessBodySchemaValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing response body schema database properties
	  */
	override val model = ResponseBodySchemaDbModel
}

