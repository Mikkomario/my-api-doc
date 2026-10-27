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
import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactory

import java.time.Instant

object NodeAuthScopeLinkData extends FromModelFactoryWithSchema[NodeAuthScopeLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("nodeId", IntType, Single("node_id")), 
			PropertyDeclaration("scopeId", IntType, Vector("authId", "auth_id", "scope_id")), 
			PropertyDeclaration("method", AnyType, isOptional = true), PropertyDeclaration("description", 
			StringType, isOptional = true), PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		NodeAuthScopeLinkData(valid("nodeId").getInt, valid("scopeId").getInt, 
			valid("method").string.map(Method.apply), valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Describes an authorization scope required by an API node. 
  * If multiple scopes are linked, they typically represent alternative auth scopes, usually for 
  * different purposes.
  * @param nodeId          ID of the node that accepts the linked authorization method.
  * @param scopeId         ID of the authorization scope required by the linked node.
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
case class NodeAuthScopeLinkData(nodeId: Int, scopeId: Int, method: Option[Method] = None,
                                 description: String = "", created: Instant = Now,
                                 deprecatedAfter: Option[Instant] = None)
	extends NodeAuthScopeLinkFactory[NodeAuthScopeLinkData] with NodeAuthLinkData 
		with NodeAuthLinkDataLike[NodeAuthScopeLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def authId = scopeId
	
	override def copyNodeAuthLink(nodeId: Int, authId: Int, method: Option[Method], description: String, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		copy(nodeId = nodeId, scopeId = authId, method = method, description = description, 
			created = created, deprecatedAfter = deprecatedAfter)
	
	override def withScopeId(scopeId: Int) = copy(scopeId = scopeId)
}

