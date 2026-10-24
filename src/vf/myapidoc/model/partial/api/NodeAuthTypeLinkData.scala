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
import vf.myapidoc.model.factory.api.NodeAuthTypeLinkFactory

import java.time.Instant

object NodeAuthTypeLinkData extends FromModelFactoryWithSchema[NodeAuthTypeLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("authTypeId", IntType, Vector("authId", "auth_id", "auth_type_id")),
		PropertyDeclaration("method", AnyType, isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		NodeAuthTypeLinkData(valid("nodeId").getInt, valid("authTypeId").getInt, 
			valid("method").string.map(Method.apply), valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Common trait for links between API nodes and possible authorization requirements
  * @param nodeId          ID of the node that accepts the linked authorization method.
  * @param authTypeId      ID of the authorization type accepted by the linked node.
  * @param method          The HTTP method that the linked authorization method is used for. 
  *                        None if the authorization method is used as a default for all methods 
  *                        (where not overridden).
  * @param description     A description concerning this authorization method's function in the 
  *                        linked node.
  * @param created         Time when this node auth link was added to the database
  * @param deprecatedAfter Time when this node auth link became deprecated. None while this node 
  *                        auth link is still valid.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class NodeAuthTypeLinkData(nodeId: Int, authTypeId: Int, method: Option[Method] = None, description: String = "",
                                created: Instant = Now, deprecatedAfter: Option[Instant] = None)
	extends NodeAuthTypeLinkFactory[NodeAuthTypeLinkData] with NodeAuthLinkData 
		with NodeAuthLinkDataLike[NodeAuthTypeLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def copyNodeAuthLink(nodeId: Int, authId: Int, method: Option[Method], description: String, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		copy(nodeId = nodeId, authTypeId = authId, method = method, description = description, 
			created = created, deprecatedAfter = deprecatedAfter)
	
	override def withAuthTypeId(authTypeId: Int) = copy(authTypeId = authTypeId)
}

