package vf.myapidoc.database.access.api.params.query

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.node.impl.link.model.AccessApiNodeMethodImplementationModelSchemaLinkValues
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel

/**
  * Used for accessing query parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessQueryParametersSchemaLinkValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing query parameters schema link database properties
	  */
	override val model = QueryParametersSchemaLinkDbModel
	
	/**
	  * ID of the method implementation that receives the linked parameters.
	  */
	lazy val implementationIds = apply(model.implementationId) { v => v.getInt }
	/**
	  * ID of the schema that defines the accepted path parameters.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	
	
	// COMPUTED -----------------------
	
	/**
	 * Links model type IDs to method implementations.
	 * Assumes that only 0-1 model types are linked to any targeted implementation.
	 * @param connection Implicit DB connection
	 * @return A map where keys are API node method implementation IDs and values are linked model type IDs.
	 */
	def modelIdPerImplementationId(implicit connection: Connection) =
		access.toMap(model.implementationId, model.modelTypeId) { _.getInt } { _.getInt }
}

