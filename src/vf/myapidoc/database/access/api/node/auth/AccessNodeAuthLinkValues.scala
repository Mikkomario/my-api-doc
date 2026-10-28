package vf.myapidoc.database.access.api.node.auth

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessValues
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

/**
  * Used for accessing node auth link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AccessNodeAuthLinkValues extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * ID of the node that accepts the linked authorization method.
	  */
	lazy val nodeIds = apply(model.nodeId) { v => v.getInt }
	/**
	  * ID of the authorization element accepted by the linked node.
	  */
	lazy val authIds = apply(model.authId) { v => v.getInt }
	/**
	  * The HTTP method that the linked authorization method is used for. 
	  * None if the authorization method is used as a default for all methods (where not overridden).
	  */
	lazy val methods = apply(model.method).flatten { v => v.string.map(Method.apply) } { _.name }
	/**
	  * A description concerning this authorization method's function in the linked node.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing node auth link database properties
	  */
	def model: NodeAuthLinkDbProps
}

