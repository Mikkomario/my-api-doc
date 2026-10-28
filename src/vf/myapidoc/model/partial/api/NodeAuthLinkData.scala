package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.Single
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{AnyType, InstantType, IntType, StringType}

import java.time.Instant

object NodeAuthLinkData extends FromModelFactoryWithSchema[NodeAuthLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("authId", IntType, Single("auth_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("method", AnyType, isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		NodeAuthLinkData(valid("originalUpdateId").getInt, valid("nodeId").getInt, valid("authId").getInt, 
			valid("removingUpdateId").int, valid("method").string.map(Method.apply), 
			valid("description").getString)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new node auth link
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param nodeId           ID of the node that accepts the linked authorization method.
	  * @param authId           ID of the authorization element accepted by the linked node.
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @param method           The HTTP method that the linked authorization method is used for. 
	  *                         None if the authorization method is used as a default for all methods 
	  *                         (where not overridden).
	  * @param description      A description concerning this authorization method's function in the 
	  *                         linked node.
	  * @return node auth link with the specified properties
	  */
	def apply(originalUpdateId: Int, nodeId: Int, authId: Int, removingUpdateId: Option[Int] = None, 
		method: Option[Method] = None, description: String = ""): NodeAuthLinkData = 
		_NodeAuthLinkData(originalUpdateId, nodeId, authId, removingUpdateId, method, description)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the node auth link data trait
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param nodeId           ID of the node that accepts the linked authorization method.
	  * @param authId           ID of the authorization element accepted by the linked node.
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @param method           The HTTP method that the linked authorization method is used for. 
	  *                         None if the authorization method is used as a default for all methods 
	  *                         (where not overridden).
	  * @param description      A description concerning this authorization method's function in the 
	  *                         linked node.
	  * @author Mikko Hilpinen
	  * @since 22.10.2026
	  */
	private case class _NodeAuthLinkData(originalUpdateId: Int, nodeId: Int, authId: Int, 
		removingUpdateId: Option[Int] = None, method: Option[Method] = None, description: String = "") 
		extends NodeAuthLinkData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param originalUpdateId ID of the update that introduced this element (version)
		  * @param nodeId           ID of the node that accepts the linked authorization method.
		  * @param authId           ID of the authorization element accepted by the linked node.
		  * @param removingUpdateId ID of the update that removed or replaced this element (version)
		  * @param method           The HTTP method that the linked authorization method is used for. 
		  *                         None if the authorization method is used as a default for all methods 
		  *                         (where not overridden).
		  * @param description      A description concerning this authorization method's function in the 
		  *                         linked node.
		  */
		override def copyNodeAuthLink(originalUpdateId: Int, nodeId: Int, authId: Int, 
			removingUpdateId: Option[Int] = None, method: Option[Method] = None, description: String = "") = 
			_NodeAuthLinkData(originalUpdateId, nodeId, authId, removingUpdateId, method, description)
	}
}

/**
  * Common trait for links between API nodes and possible authorization requirements
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkData 
	extends NodeAuthLinkDataLike[NodeAuthLinkData] with VersionedDocElementData 
		with VersionedDocElementDataLike[NodeAuthLinkData]

