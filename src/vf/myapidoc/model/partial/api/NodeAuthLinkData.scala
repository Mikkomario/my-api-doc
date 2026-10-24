package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.Single
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.time.Now

import java.time.Instant

object NodeAuthLinkData extends FromModelFactoryWithSchema[NodeAuthLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("nodeId", IntType, Single("node_id")), 
			PropertyDeclaration("authId", IntType, Single("auth_id")), PropertyDeclaration("method", AnyType, 
			isOptional = true), PropertyDeclaration("description", StringType, isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		NodeAuthLinkData(valid("nodeId").getInt, valid("authId").getInt, 
			valid("method").string.map(Method.apply), valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new node auth link
	  * @param nodeId          ID of the node that accepts the linked authorization method.
	  * @param authId          ID of the authorization element accepted by the linked node.
	  * @param method          The HTTP method that the linked authorization method is used for. 
	  *                        None if the authorization method is used as a default for all methods 
	  *                        (where not overridden).
	  * @param description     A description concerning this authorization method's function in the 
	  *                        linked node.
	  * @param created         Time when this node auth link was added to the database
	  * @param deprecatedAfter Time when this node auth link became deprecated. None while this node 
	  *                        auth link is still valid.
	  * @return node auth link with the specified properties
	  */
	def apply(nodeId: Int, authId: Int, method: Option[Method] = None, description: String = "", 
		created: Instant = Now, deprecatedAfter: Option[Instant] = None): NodeAuthLinkData = 
		_NodeAuthLinkData(nodeId, authId, method, description, created, deprecatedAfter)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the node auth link data trait
	  * @param nodeId          ID of the node that accepts the linked authorization method.
	  * @param authId          ID of the authorization element accepted by the linked node.
	  * @param method          The HTTP method that the linked authorization method is used for. 
	  *                        None if the authorization method is used as a default for all methods 
	  *                        (where not overridden).
	  * @param description     A description concerning this authorization method's function in the 
	  *                        linked node.
	  * @param created         Time when this node auth link was added to the database
	  * @param deprecatedAfter Time when this node auth link became deprecated. None while this node 
	  *                        auth link is still valid.
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _NodeAuthLinkData(nodeId: Int, authId: Int, method: Option[Method] = None, 
		description: String = "", created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
		extends NodeAuthLinkData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param nodeId          ID of the node that accepts the linked authorization method.
		  * @param authId          ID of the authorization element accepted by the linked node.
		  * @param method          The HTTP method that the linked authorization method is used for. 
		  *                        None if the authorization method is used as a default for all methods 
		  *                        (where not overridden).
		  * @param description     A description concerning this authorization method's function in the 
		  *                        linked node.
		  * @param created         Time when this node auth link was added to the database
		  * @param deprecatedAfter Time when this node auth link became deprecated. None while this node 
		  *                        auth link is still valid.
		  */
		override def copyNodeAuthLink(nodeId: Int, authId: Int, method: Option[Method] = None, 
			description: String = "", created: Instant = Now, deprecatedAfter: Option[Instant] = None) = 
			_NodeAuthLinkData(nodeId, authId, method, description, created, deprecatedAfter)
	}
}

/**
  * Common trait for links between API nodes and possible authorization requirements
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkData extends NodeAuthLinkDataLike[NodeAuthLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this node auth link has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this node auth link is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
}

