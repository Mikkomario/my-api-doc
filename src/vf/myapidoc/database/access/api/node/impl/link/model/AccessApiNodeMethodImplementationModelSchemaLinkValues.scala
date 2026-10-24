package vf.myapidoc.database.access.api.node.impl.link.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.AccessValues
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

/**
  * Used for accessing api node method implementation model schema link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AccessApiNodeMethodImplementationModelSchemaLinkValues extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Access to api node method implementation model schema link ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the method implementation that uses the linked schema.
	  */
	lazy val implementationIds = apply(model.implementationId) { v => v.getInt }
	/**
	  * ID of the schema that defines the type of model accepted by the linked implementation.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	/**
	  * Time when this api node method implementation model schema link was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this api node method implementation model schema link became deprecated. None while 
	  * this api node method implementation model schema link is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing api node method implementation model schema link database properties
	  */
	def model: ApiNodeMethodImplementationModelSchemaLinkDbProps
	
	
	// COMPUTED --------------------
	
	/**
	 * Links model type IDs to method implementations.
	 * Assumes that only 0-1 model types are linked to any targeted implementation.
	 * @param connection Implicit DB connection
	 * @return A map where keys are API node method implementation IDs and values are linked model type IDs.
	 */
	def modelIdPerImplementationId(implicit connection: Connection) =
		access.toMap(model.implementationId, model.modelTypeId) { _.getInt } { _.getInt }
}

