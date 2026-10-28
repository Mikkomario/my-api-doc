package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{AnyType, BooleanType, InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationFactory

import java.time.Instant

object ApiNodeMethodImplementationData extends FromModelFactoryWithSchema[ApiNodeMethodImplementationData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("method", AnyType, Empty, Method.Get.name),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("requiresRequestBody", BooleanType, Single("requires_request_body"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeMethodImplementationData(valid("originalUpdateId").getInt, valid("nodeId").getInt, 
			valid("removingUpdateId").int, Method(valid("method").getString), valid("description").getString, 
			valid("requiresRequestBody").getBoolean)
}

/**
  * Describes how a specific HTTP method is implemented in an API node.
  * @param originalUpdateId    ID of the update that introduced this element (version)
  * @param nodeId              ID of the API node that contains this implementation.
  * @param removingUpdateId    ID of the update that removed or replaced this element (version)
  * @param method              The implemented method.
  * @param description         A description of this implementation.
  * @param requiresRequestBody Whether a request body is required for this implementation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class ApiNodeMethodImplementationData(originalUpdateId: Int, nodeId: Int, removingUpdateId: Option[Int] = None,
                                           method: Method = Method.Get, description: String = "",
                                           requiresRequestBody: Boolean = false)
	extends ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationData] with VersionedDocElementData 
		with VersionedDocElementDataLike[ApiNodeMethodImplementationData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("nodeId" -> nodeId,
		"method" -> method.name, "description" -> description, "requiresRequestBody" -> requiresRequestBody))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDescription(description: String) = copy(description = description)
	override def withMethod(method: Method) = copy(method = method)
	override def withNodeId(nodeId: Int) = copy(nodeId = nodeId)
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = copy(requiresRequestBody = requiresRequestBody)
}

