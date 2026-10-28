package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel

/**
  * Used for accessing request body schema values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessRequestBodySchemaValues(access: AccessManyColumns) extends AccessBodySchemaValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing request body schema database properties
	  */
	override val model = RequestBodySchemaDbModel
	
	/**
	  * Non-standard content type expected from the request body, if applicable.
	  */
	lazy val customContentTypes = apply(model.customContentType) { v => v.getString }
	/**
	  * Description of the expected request body.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
}

