package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{BooleanType, InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.factory.api.PossibleResponseStatusFactory

import java.time.Instant

object PossibleResponseStatusData extends FromModelFactoryWithSchema[PossibleResponseStatusData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("implementationId", IntType, Single("implementation_id")),
		PropertyDeclaration("statusCode", IntType, Single("status_code")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("hasBody", BooleanType, Single("has_body"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PossibleResponseStatusData(valid("originalUpdateId").getInt, valid("implementationId").getInt, 
			valid("statusCode").getInt, valid("removingUpdateId").int, valid("description").getString, 
			UncertainBoolean(valid("hasBody").boolean))
}

/**
  * Defines a possible response status for an API node method call.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param implementationId ID of the implementation that may return this response status.
  * @param statusCode       HTTP status code that may be returned by the API node method call.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param description      Description of what this status signifies.
  * @param hasBody          Whether a response body is returned with this status. Uncertain if 
  *                         not specified in the documentation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class PossibleResponseStatusData(originalUpdateId: Int, implementationId: Int, statusCode: Int,
                                      removingUpdateId: Option[Int] = None, description: String = "",
                                      hasBody: UncertainBoolean = UncertainBoolean)
	extends PossibleResponseStatusFactory[PossibleResponseStatusData] with VersionedDocElementData 
		with VersionedDocElementDataLike[PossibleResponseStatusData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("implementationId" -> implementationId,
		"statusCode" -> statusCode, "description" -> description, "hasBody" -> hasBody.exact))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDescription(description: String) = copy(description = description)
	override def withHasBody(hasBody: UncertainBoolean) = copy(hasBody = hasBody)
	override def withImplementationId(implementationId: Int) = copy(implementationId = implementationId)
	override def withStatusCode(statusCode: Int) = copy(statusCode = statusCode)
}

