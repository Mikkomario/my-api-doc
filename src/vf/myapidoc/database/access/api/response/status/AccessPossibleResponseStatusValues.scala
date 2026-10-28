package vf.myapidoc.database.access.api.response.status

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.util.UncertainBoolean
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.PossibleResponseStatusDbModel

/**
  * Used for accessing possible response status values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessPossibleResponseStatusValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing possible response status database properties
	  */
	override val model = PossibleResponseStatusDbModel
	
	/**
	  * ID of the implementation that may return this response status.
	  */
	lazy val implementationIds = apply(model.implementationId) { v => v.getInt }
	/**
	  * HTTP status code that may be returned by the API node method call.
	  */
	lazy val statusCodes = apply(model.statusCode) { v => v.getInt }
	/**
	  * Description of what this status signifies.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	/**
	  * Whether a response body is returned with this status. Uncertain if not specified in the 
	  * documentation.
	  */
	lazy val haveBodies = apply(model.hasBody) { v => UncertainBoolean(v.boolean) }
}

