package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.{FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{BooleanType, InstantType, IntType}
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.factory.api.ResponseBodySchemaFactory

import java.time.Instant

object ResponseBodySchemaData 
	extends FromModelFactoryWithSchema[ResponseBodySchemaData] with FromModelFactory[ResponseBodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("statusId", IntType, Vector("parentId", "parent_id", "status_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("dataType", IntType, Single("data_type"), AnyProp.id),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ResponseBodySchemaData(valid("originalUpdateId").getInt, valid("statusId").getInt, 
			valid("removingUpdateId").int, PropertyType.fromValue(valid("dataType")), 
			valid("modelTypeId").int, valid("isArray").getBoolean)
}

/**
  * Defines how an API node's response body is formed.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param statusId         ID of the status this type of response body is returned with.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param dataType         Type of this body's contents.
  * @param modelTypeId      If this body contains an object or objects, contains the ID of the 
  *                         model schema that describes the applicable object structure.
  * @param isArray          Whether this body contains an array of values. False if no more than 
  *                         one value may be included.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class ResponseBodySchemaData(originalUpdateId: Int, statusId: Int, removingUpdateId: Option[Int] = None,
                                  dataType: PropertyType = AnyProp, modelTypeId: Option[Int] = None,
                                  isArray: Boolean = false)
	extends ResponseBodySchemaFactory[ResponseBodySchemaData] with BodySchemaData 
		with BodySchemaDataLike[ResponseBodySchemaData]
{
	// IMPLEMENTED	--------------------
	
	override def parentId = statusId
	
	override def copyBodySchema(originalUpdateId: Int, parentId: Int, removingUpdateId: Option[Int], 
		dataType: PropertyType, modelTypeId: Option[Int], isArray: Boolean) = 
		copy(originalUpdateId = originalUpdateId, statusId = parentId, removingUpdateId = removingUpdateId, 
			dataType = dataType, modelTypeId = modelTypeId, isArray = isArray)
	
	override def withStatusId(statusId: Int) = copy(statusId = statusId)
}

