package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.access.api.node.impl.link.model.AccessApiNodeMethodImplementationModelSchemaLinkValue
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel

/**
  * Used for accessing individual request body schema values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessRequestBodySchemaValue(access: AccessColumn) extends AccessBodySchemaValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing request body schema database properties
	  */
	override val model = RequestBodySchemaDbModel
	
	/**
	  * Non-standard content type expected from the request body, if applicable.
	  */
	lazy val customContentType = apply(model.customContentType) { v => v.getString }
	/**
	  * Description of the expected request body.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	/**
	  * Whether a JSON array is expected.
	  */
	lazy val isArray = apply(model.isArray).optional { v => v.boolean }
}

