package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.Single
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{AnyType, InstantType, IntType, StringType}
import vf.myapidoc.model.factory.api.NodeAuthTypeLinkFactory

import java.time.Instant

object NodeAuthTypeLinkData extends FromModelFactoryWithSchema[NodeAuthTypeLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("authTypeId", IntType, Vector("authId", "auth_id", "auth_type_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("method", AnyType, isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		NodeAuthTypeLinkData(valid("originalUpdateId").getInt, valid("nodeId").getInt, 
			valid("authTypeId").getInt, valid("removingUpdateId").int, 
			valid("method").string.map(Method.apply), valid("description").getString)
}

/**
  * Describes an authorization type accepted by an API node.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param nodeId           ID of the node that accepts the linked authorization method.
  * @param authTypeId       ID of the authorization type accepted by the linked node.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param method           The HTTP method that the linked authorization method is used for. 
  *                         None if the authorization method is used as a default for all methods 
  *                         (where not overridden).
  * @param description      A description concerning this authorization method's function in the 
  *                         linked node.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class NodeAuthTypeLinkData(originalUpdateId: Int, nodeId: Int, authTypeId: Int, 
	removingUpdateId: Option[Int] = None, method: Option[Method] = None, description: String = "") 
	extends NodeAuthTypeLinkFactory[NodeAuthTypeLinkData] with NodeAuthLinkData 
		with NodeAuthLinkDataLike[NodeAuthTypeLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def copyNodeAuthLink(originalUpdateId: Int, nodeId: Int, authId: Int, 
		removingUpdateId: Option[Int], method: Option[Method], description: String) = 
		copy(originalUpdateId = originalUpdateId, nodeId = nodeId, authTypeId = authId, 
			removingUpdateId = removingUpdateId, method = method, description = description)
	
	override def withAuthTypeId(authTypeId: Int) = copy(authTypeId = authTypeId)
}

