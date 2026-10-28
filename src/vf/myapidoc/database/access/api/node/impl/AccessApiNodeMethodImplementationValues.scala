package vf.myapidoc.database.access.api.node.impl

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel

/**
  * Used for accessing api node method implementation values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementationValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node method implementation database properties
	  */
	override val model = ApiNodeMethodImplementationDbModel
	
	/**
	  * ID of the API node that contains this implementation.
	  */
	lazy val nodeIds = apply(model.nodeId) { v => v.getInt }
	/**
	  * The implemented method.
	  */
	lazy val methods = apply(model.method) { v => Method(v.getString) } { _.name }
	/**
	  * A description of this implementation.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	/**
	  * Whether a request body is required for this implementation.
	  */
	lazy val requireRequestBodies = apply(model.requiresRequestBody) { v => v.getBoolean }
}

