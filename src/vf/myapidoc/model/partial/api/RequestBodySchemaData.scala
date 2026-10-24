package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.RequestBodySchemaFactory

import java.time.Instant

object RequestBodySchemaData extends FromModelFactoryWithSchema[RequestBodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("implementationId", IntType, Single("implementation_id")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true),
		PropertyDeclaration("customContentType", StringType, Single("custom_content_type"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		RequestBodySchemaData(valid("implementationId").getInt, valid("modelTypeId").getInt, 
			valid("created").getInstant, valid("deprecatedAfter").instant, 
			valid("customContentType").getString, valid("description").getString, valid("isArray").getBoolean)
}

/**
  * Applies a specific schema to the request body accepted by an API node. 
  * Multiple alternative schemas may be defined separately.
  * @param implementationId  ID of the method implementation that uses the linked schema.
  * @param modelTypeId       ID of the schema that defines the type of model accepted by the 
  *                          linked implementation.
  * @param created           Time when this api node method implementation model schema link was 
  *                          added to the database
  * @param deprecatedAfter   Time when this api node method implementation model schema link 
  *                          became deprecated. None while this api node method implementation 
  *                          model schema link is still valid.
  * @param customContentType Non-standard content type expected from the request body, if 
  *                          applicable.
  * @param description       Description of the expected request body.
  * @param isArray           Whether a JSON array is expected.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class RequestBodySchemaData(implementationId: Int, modelTypeId: Int, created: Instant = Now,
                                 deprecatedAfter: Option[Instant] = None, customContentType: String = "",
                                 description: String = "", isArray: Boolean = false)
	extends RequestBodySchemaFactory[RequestBodySchemaData] 
		with ApiNodeMethodImplementationModelSchemaLinkData 
		with ApiNodeMethodImplementationModelSchemaLinkDataLike[RequestBodySchemaData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		super[ApiNodeMethodImplementationModelSchemaLinkData].toModel ++ Model(Vector("customContentType" -> customContentType, 
			"description" -> description, "isArray" -> isArray))
	
	override def copyApiNodeMethodImplementationModelSchemaLink(implementationId: Int, modelTypeId: Int, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		copy(implementationId = implementationId, modelTypeId = modelTypeId, created = created, 
			deprecatedAfter = deprecatedAfter)
	
	override def withCustomContentType(customContentType: String) = copy(customContentType = 
		customContentType)
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withIsArray(isArray: Boolean) = copy(isArray = isArray)
}

