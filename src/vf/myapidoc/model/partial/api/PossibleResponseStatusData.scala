package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.factory.api.PossibleResponseStatusFactory

import java.time.Instant

object PossibleResponseStatusData extends FromModelFactoryWithSchema[PossibleResponseStatusData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("implementationId", IntType, 
			Single("implementation_id")), PropertyDeclaration("statusCode", IntType, Single("status_code")), 
			PropertyDeclaration("description", StringType, isOptional = true), PropertyDeclaration("created", 
			InstantType, isOptional = true), PropertyDeclaration("deprecatedAfter", InstantType, 
			Single("deprecated_after"), isOptional = true), PropertyDeclaration("hasBody", BooleanType, 
			Single("has_body"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PossibleResponseStatusData(valid("implementationId").getInt, valid("statusCode").getInt, 
			valid("description").getString, valid("created").getInstant, valid("deprecatedAfter").instant, 
			UncertainBoolean(valid("hasBody").boolean))
}

/**
  * Defines a possible response status for an API node method call.
  * @param implementationId ID of the implementation that may return this response status.
  * @param statusCode       HTTP status code that may be returned by the API node method call.
  * @param description      Description of what this status signifies.
  * @param created          Time when this possible response status was added to the database
  * @param deprecatedAfter  Time when this possible response status became deprecated. None while 
  *                         this possible response status is still valid.
  * @param hasBody          Whether a response body is returned with this status. Uncertain if 
  *                         not specified in the documentation.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class PossibleResponseStatusData(implementationId: Int, statusCode: Int, description: String = "", 
	created: Instant = Now, deprecatedAfter: Option[Instant] = None, hasBody: UncertainBoolean = UncertainBoolean)
	extends PossibleResponseStatusFactory[PossibleResponseStatusData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this possible response status has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this possible response status is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("implementationId" -> implementationId, "statusCode" -> statusCode,
		"description" -> description, "created" -> created, "deprecatedAfter" -> deprecatedAfter,
		"hasBody" -> hasBody.exact))
	
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withHasBody(hasBody: UncertainBoolean) = copy(hasBody = hasBody)
	override def withImplementationId(implementationId: Int) = copy(implementationId = implementationId)
	override def withStatusCode(statusCode: Int) = copy(statusCode = statusCode)
}

