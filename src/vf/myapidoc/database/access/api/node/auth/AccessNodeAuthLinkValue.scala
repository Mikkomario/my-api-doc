package vf.myapidoc.database.access.api.node.auth

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

/**
  * Used for accessing individual node auth link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AccessNodeAuthLinkValue extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * ID of the node that accepts the linked authorization method.
	  */
	lazy val nodeId = apply(model.nodeId).optional { v => v.int }
	/**
	  * ID of the authorization element accepted by the linked node.
	  */
	lazy val authId = apply(model.authId).optional { v => v.int }
	/**
	  * The HTTP method that the linked authorization method is used for. 
	  * None if the authorization method is used as a default for all methods (where not overridden).
	  */
	lazy val method = apply(model.method).optional { v => v.string.map(Method.apply) } { _.name }
	/**
	  * A description concerning this authorization method's function in the linked node.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing node auth link database properties
	  */
	def model: NodeAuthLinkDbProps
}

