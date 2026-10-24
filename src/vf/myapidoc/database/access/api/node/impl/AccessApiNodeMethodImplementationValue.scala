package vf.myapidoc.database.access.api.node.impl

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel

/**
  * Used for accessing individual api node method implementation values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementationValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node method implementation database properties
	  */
	val model = ApiNodeMethodImplementationDbModel
	
	/**
	  * Access to api node method implementation id
	  */
	lazy val id = apply(model.index).optional { _.int }
	/**
	  * ID of the API node that contains this implementation.
	  */
	lazy val nodeId = apply(model.nodeId).optional { v => v.int }
	/**
	  * The implemented method.
	  */
	lazy val method = apply(model.method).optional { v => v.string.map(Method.apply) } { _.name }
	/**
	  * A description of this implementation.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	/**
	  * Time when this api node method implementation was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	/**
	  * Time when this api node method implementation became deprecated. None while this api node 
	  * method implementation is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
	/**
	  * Whether a request body is required for this implementation.
	  */
	lazy val requiresRequestBody = apply(model.requiresRequestBody).optional { v => v.boolean }
}

