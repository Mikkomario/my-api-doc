package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.ResponseBodySchemaFactory

import java.time.Instant

object ResponseBodySchemaData extends FromModelFactory[ResponseBodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("statusId", IntType, Vector("parentId", "parent_id", "status_id")),
		PropertyDeclaration("dataType", IntType, Single("data_type")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: HasProperties) = schema.validate(model).map { valid =>
		ResponseBodySchemaData(valid("statusId").getInt, PropertyType.fromValue(valid("dataType")),
			valid("modelTypeId").int, valid("created").getInstant, valid("deprecatedAfter").instant,
			valid("isArray").getBoolean)
	}
}

/**
  * Defines how an API node's response body is formed.
  * @param statusId        ID of the status this type of response body is returned with.
  * @param dataType        Type of this body's contents.
  * @param modelTypeId     If this body contains an object or objects, contains the ID of the 
  *                        model schema that describes the applicable object structure.
  * @param created         Time when this body schema was added to the database
  * @param deprecatedAfter Time when this body schema became deprecated. None while this body 
  *                        schema is still valid.
  * @param isArray         Whether this body contains an array of values. False if no more than 
  *                        one value may be included.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class ResponseBodySchemaData(statusId: Int, dataType: PropertyType, modelTypeId: Option[Int] = None, 
	created: Instant = Now, deprecatedAfter: Option[Instant] = None, isArray: Boolean = false) 
	extends ResponseBodySchemaFactory[ResponseBodySchemaData] with BodySchemaData 
		with BodySchemaDataLike[ResponseBodySchemaData]
{
	// IMPLEMENTED	--------------------
	
	override def parentId = statusId
	
	override def copyBodySchema(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int], 
		created: Instant, deprecatedAfter: Option[Instant], isArray: Boolean) = 
		copy(statusId = parentId, dataType = dataType, modelTypeId = modelTypeId, created = created, 
			deprecatedAfter = deprecatedAfter, isArray = isArray)
	
	override def withStatusId(statusId: Int) = copy(statusId = statusId)
}

