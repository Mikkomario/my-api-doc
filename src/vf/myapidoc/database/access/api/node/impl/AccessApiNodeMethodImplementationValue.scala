package vf.myapidoc.database.access.api.node.impl

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel

/**
  * Used for accessing individual api node method implementation values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementationValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node method implementation database properties
	  */
	override val model = ApiNodeMethodImplementationDbModel
	
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
	  * Whether a request body is required for this implementation.
	  */
	lazy val requiresRequestBody = apply(model.requiresRequestBody).optional { v => v.boolean }
}

