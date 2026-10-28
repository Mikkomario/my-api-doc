package vf.myapidoc.database.access.api.response.status

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.util.UncertainBoolean
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.PossibleResponseStatusDbModel

/**
  * Used for accessing individual possible response status values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessPossibleResponseStatusValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing possible response status database properties
	  */
	override val model = PossibleResponseStatusDbModel
	
	/**
	  * ID of the implementation that may return this response status.
	  */
	lazy val implementationId = apply(model.implementationId).optional { v => v.int }
	/**
	  * HTTP status code that may be returned by the API node method call.
	  */
	lazy val statusCode = apply(model.statusCode).optional { v => v.int }
	/**
	  * Description of what this status signifies.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	/**
	  * Whether a response body is returned with this status. Uncertain if not specified in the 
	  * documentation.
	  */
	lazy val hasBody = apply(model.hasBody) { v => UncertainBoolean(v.boolean) }
}

