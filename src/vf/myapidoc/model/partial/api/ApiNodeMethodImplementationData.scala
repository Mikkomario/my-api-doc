package vf.myapidoc.model.partial.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationFactory

import java.time.Instant

object ApiNodeMethodImplementationData extends FromModelFactoryWithSchema[ApiNodeMethodImplementationData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("nodeId", IntType, Single("node_id")), 
			PropertyDeclaration("method", AnyType, Empty, Method.Get.name), 
			PropertyDeclaration("description", StringType, isOptional = true), PropertyDeclaration("created", 
			InstantType, isOptional = true), PropertyDeclaration("deprecatedAfter", InstantType, 
			Single("deprecated_after"), isOptional = true), PropertyDeclaration("requiresRequestBody", 
			BooleanType, Single("requires_request_body"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeMethodImplementationData(valid("nodeId").getInt, Method(valid("method").getString), 
			valid("description").getString, valid("created").getInstant, valid("deprecatedAfter").instant, 
			valid("requiresRequestBody").getBoolean)
}

/**
  * Describes how a specific HTTP method is implemented in an API node.
  * @param nodeId              ID of the API node that contains this implementation.
  * @param method              The implemented method.
  * @param description         A description of this implementation.
  * @param created             Time when this api node method implementation was added to the 
  *                            database
  * @param deprecatedAfter     Time when this api node method implementation became deprecated. 
  *                            None while this api node method implementation is still valid.
  * @param requiresRequestBody Whether a request body is required for this implementation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class ApiNodeMethodImplementationData(nodeId: Int, method: Method = Method.Get, description: String = "",
                                           created: Instant = Now, deprecatedAfter: Option[Instant] = None,
                                           requiresRequestBody: Boolean = false)
	extends ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this api node method implementation has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this api node method implementation is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("nodeId" -> nodeId, "method" -> method.name, "description" -> description,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter, "requiresRequestBody" -> requiresRequestBody))
	
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withMethod(method: Method) = copy(method = method)
	override def withNodeId(nodeId: Int) = copy(nodeId = nodeId)
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		copy(requiresRequestBody = requiresRequestBody)
}

