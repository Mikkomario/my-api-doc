package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.cached.NewProperty
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.factory.schema.PropertySchemaFactory
import vf.myapidoc.model.template.ClientModelConvertible

import java.time.Instant

object PropertySchemaData extends FromModelFactory[PropertySchemaData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("name", StringType, isOptional = true),
		PropertyDeclaration("dataType", IntType, Single("data_type")),
		PropertyDeclaration("contentTypeId", IntType, Single("content_type_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false, isOptional = true),
		PropertyDeclaration("isOptional", BooleanType, Single("is_optional"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: HasProperties) = schema.validate(model).map { valid =>
		PropertySchemaData(valid("modelTypeId").getInt, valid("name").getString,
			PropertyType.fromValue(valid("dataType")),
			valid("contentTypeId").int, valid("description").getString, valid("created").getInstant,
			valid("deprecatedAfter").instant, UncertainBoolean(valid("isArray").boolean),
			valid("isOptional").getBoolean)
	}
}

/**
  * Defines an individual property in a model schema.
  * @param modelTypeId     ID of the model type that this property belongs to.
  * @param name            Name of this property, as it should appear in the input or output 
  *                        model / JSON object.
  * @param dataType        This property's expected value type.
  * @param contentTypeId   If this property contains a model/object, this is the ID of the 
  *                        contained model's type. 
  *                        None if this property uses some other data type.
  * @param description     A description of this property's role/function.
  * @param created         Time when this property schema was added to the database
  * @param deprecatedAfter Time when this property schema became deprecated. None while this 
  *                        property schema is still valid.
  * @param isArray         Whether this property is an array of values. 
  *                        False if this property contains a single value. Uncertain if both are 
  *                        possible.
  * @param isOptional      Whether this property may be omitted from the input and/or output.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PropertySchemaData(modelTypeId: Int, name: String, dataType: PropertyType,
                              contentTypeId: Option[Int] = None, description: String = "", created: Instant = Now,
                              deprecatedAfter: Option[Instant] = None, isArray: UncertainBoolean = false,
                              isOptional: Boolean = false)
	extends PropertySchemaFactory[PropertySchemaData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this property schema has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this property schema is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("modelTypeId" -> modelTypeId, "name" -> name, "dataType" -> dataType.id,
		"contentTypeId" -> contentTypeId, "description" -> description, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter, "isArray" -> isArray.exact, "isOptional" -> isOptional))
	
	override def withContentTypeId(contentTypeId: Int) = copy(contentTypeId = Some(contentTypeId))
	override def withCreated(created: Instant) = copy(created = created)
	override def withDataType(dataType: PropertyType) = copy(dataType = dataType)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withIsArray(isArray: UncertainBoolean) = copy(isArray = isArray)
	override def withIsOptional(isOptional: Boolean) = copy(isOptional = isOptional)
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
	override def withName(name: String) = copy(name = name)
	
	
	// OTHER    ------------------------
	
	def ~==(update: NewProperty) =
		isArray == update.isArray && isOptional == update.isOptional &&
			dataType == update.dataTypes.headOption.getOrElse(AnyProp)
	/**
	 * Checks whether this schema conflicts with the specified property update.
	 * Assumes that property names are an exact match.
	 * NB: Doesn't check referenced model types or alternative data types, since those are not
	 * included in this model.
	 */
	def !~==(update: NewProperty) = !(this ~== update)
}

