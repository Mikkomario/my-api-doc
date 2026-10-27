package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{BooleanType, InstantType, IntType, StringType}
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.RequestBodySchemaFactory

import java.time.Instant

object RequestBodySchemaData extends FromModelFactoryWithSchema[RequestBodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("implementationId", IntType, Vector("implementation_id", "parentId", "parent_id")),
		PropertyDeclaration("dataType", IntType, Single("data_type")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false),
		PropertyDeclaration("customContentType", StringType, Single("custom_content_type"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) =
		RequestBodySchemaData(valid("implementationId").getInt, PropertyType.fromValue(valid("dataType")),
			valid("modelTypeId").int, valid("created").getInstant, valid("deprecatedAfter").instant,
			valid("isArray").getBoolean, valid("customContentType").getString, valid("description").getString)
}

/**
  * Applies a specific schema to the request body accepted by an API node. 
  * Multiple alternative schemas may be defined separately.
  * @param implementationId  ID of the method implementation that receives this request body.
  * @param dataType          Type of this body's contents.
  * @param modelTypeId       If this body contains an object or objects, contains the ID of the 
  *                          model schema that describes the applicable object structure.
  * @param created           Time when this body schema was added to the database
  * @param deprecatedAfter   Time when this body schema became deprecated. None while this body 
  *                          schema is still valid.
  * @param isArray           Whether this body contains an array of values. False if no more than 
  *                          one value may be included.
  * @param customContentType Non-standard content type expected from the request body, if 
  *                          applicable.
  * @param description       Description of the expected request body.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class RequestBodySchemaData(implementationId: Int, dataType: PropertyType, modelTypeId: Option[Int] = None,
                                 created: Instant = Now, deprecatedAfter: Option[Instant] = None,
                                 isArray: Boolean = false, customContentType: String = "", description: String = "")
	extends RequestBodySchemaFactory[RequestBodySchemaData] with BodySchemaData 
		with BodySchemaDataLike[RequestBodySchemaData]
{
	// IMPLEMENTED	--------------------
	
	override def parentId = implementationId
	
	override def toModel = super[BodySchemaData].toModel ++ Model(Pair("customContentType" -> customContentType,
		"description" -> description))
	
	override def copyBodySchema(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int], created: Instant,
	                            deprecatedAfter: Option[Instant], isArray: Boolean) =
		copy(implementationId = parentId, dataType = dataType, modelTypeId = modelTypeId, created = created, 
			deprecatedAfter = deprecatedAfter, isArray = isArray)
	
	override def withCustomContentType(customContentType: String) = copy(customContentType =customContentType)
	override def withDescription(description: String) = copy(description = description)
	override def withImplementationId(implementationId: Int) = copy(implementationId = implementationId)
	override def withIsArray(isArray: Boolean) = copy(isArray = isArray)
}

