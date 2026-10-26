package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration, Value}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.ResponseContentTypeFactory

import java.time.Instant

object ResponseContentTypeData extends FromModelFactoryWithSchema[ResponseContentTypeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("statusId", IntType, Single("status_id")), 
			PropertyDeclaration("contentType", IntType, Single("content_type"), isOptional = true), 
			PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true), PropertyDeclaration("isArray", BooleanType, Single("is_array"), false), 
			PropertyDeclaration("isEmpty", BooleanType, Single("is_empty"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ResponseContentTypeData(valid("statusId").getInt, PropertyType.findForValue(valid("contentType")), 
			valid("modelTypeId").int, valid("created").getInstant, valid("deprecatedAfter").instant, 
			valid("isArray").getBoolean, valid("isEmpty").getBoolean)
}

/**
  * Defines how an API node's response body is formed.
  * @param statusId        ID of the status this type of response body is returned with.
  * @param contentType     If this body is non-empty, specifies the type of content it contains.
  * @param modelTypeId     If this body contains a JSON object or objects, contains the ID of the 
  *                        model schema that describes the applicable object structure.
  * @param created         Time when this response content type was added to the database
  * @param deprecatedAfter Time when this response content type became deprecated. None while 
  *                        this response content type is still valid.
  * @param isArray         Whether this response body contains an array of values. False if no 
  *                        more than one value may be returned.
  * @param isEmpty         Whether this response body is empty / no response body is returned.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ResponseContentTypeData(statusId: Int, contentType: Option[PropertyType] = None,
                                   modelTypeId: Option[Int] = None, created: Instant = Now,
                                   deprecatedAfter: Option[Instant] = None, isArray: Boolean = false,
                                   isEmpty: Boolean = false)
	extends ResponseContentTypeFactory[ResponseContentTypeData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this response content type has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this response content type is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("statusId" -> statusId, 
			"contentType" -> contentType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			"modelTypeId" -> modelTypeId, "created" -> created, "deprecatedAfter" -> deprecatedAfter, 
			"isArray" -> isArray, "isEmpty" -> isEmpty))
	
	override def withContentType(contentType: PropertyType) = copy(contentType = Some(contentType))
	
	override def withCreated(created: Instant) = copy(created = created)
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withIsArray(isArray: Boolean) = copy(isArray = isArray)
	
	override def withIsEmpty(isEmpty: Boolean) = copy(isEmpty = isEmpty)
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	
	override def withStatusId(statusId: Int) = copy(statusId = statusId)
}

