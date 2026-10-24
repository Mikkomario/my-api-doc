package vf.myapidoc.database.access.api.node.impl

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel

/**
  * Used for accessing api node method implementation values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementationValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node method implementation database properties
	  */
	val model = ApiNodeMethodImplementationDbModel
	
	/**
	  * Access to api node method implementation ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
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
	  * Time when this api node method implementation was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this api node method implementation became deprecated. None while this api node 
	  * method implementation is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	/**
	  * Whether a request body is required for this implementation.
	  */
	lazy val requireRequestBodies = apply(model.requiresRequestBody) { v => v.getBoolean }
}

