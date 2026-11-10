package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.{FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{BooleanType, IntType, StringType}
import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.cached.doc.NewProperty
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.factory.schema.PropertySchemaFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}

object PropertySchemaData 
	extends FromModelFactoryWithSchema[PropertySchemaData] with FromModelFactory[PropertySchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("name", StringType, isOptional = true),
		PropertyDeclaration("dataType", IntType, Single("data_type"), AnyProp.id),
		PropertyDeclaration("contentTypeId", IntType, Single("content_type_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false, isOptional = true),
		PropertyDeclaration("isOptional", BooleanType, Single("is_optional"), false),
		PropertyDeclaration("isSensitive", BooleanType, Single("is_sensitive"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model) = PropertySchemaData(
		originalUpdateId = model("originalUpdateId").getInt,
		modelTypeId = model("modelTypeId").getInt, removingUpdateId = model("removingUpdateId").int,
		name = model("name").getString, dataType = PropertyType.fromValue(model("dataType")),
		contentTypeId = model("contentTypeId").int, description = model("description").getString,
		isArray = UncertainBoolean(model("isArray").boolean),
		isOptional = model("isOptional").getBoolean, isSensitive = model("isSensitive").getBoolean)
}

/**
  * Defines an individual property in a model schema.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param modelTypeId      ID of the model type that this property belongs to.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param name             Name of this property, as it should appear in the input or output 
  *                         model / JSON object.
  * @param dataType         This property's expected value type.
  * @param contentTypeId    If this property contains a model/object, this is the ID of the 
  *                         contained model's type. 
  *                         None if this property uses some other data type.
  * @param description      A description of this property's role/function.
  * @param isArray          Whether this property is an array of values. 
  *                         False if this property contains a single value. Uncertain if both are 
  *                         possible.
  * @param isOptional       Whether this property may be omitted from the input and/or output.
  * @param isSensitive      Whether this property contains sensitive information.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PropertySchemaData(originalUpdateId: Int, modelTypeId: Int, removingUpdateId: Option[Int] = None,
                              name: String = "", dataType: PropertyType = AnyProp, contentTypeId: Option[Int] = None,
                              description: String = "", isArray: UncertainBoolean = false, isOptional: Boolean = false,
                              isSensitive: Boolean = false)
	extends PropertySchemaFactory[PropertySchemaData] with VersionedDocElementData 
		with VersionedDocElementDataLike[PropertySchemaData]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Whether this property doesn't contain information marked as sensitive
	 */
	def isNotSensitive = !isSensitive
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector(
		"modelTypeId" -> modelTypeId, "name" -> name,
		"dataType" -> dataType.id, "contentTypeId" -> contentTypeId, "description" -> description,
		"isArray" -> isArray.exact, "isOptional" -> isOptional, "isSensitive" -> isSensitive))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withContentTypeId(contentTypeId: Int) = copy(contentTypeId = Some(contentTypeId))
	override def withDataType(dataType: PropertyType) = copy(dataType = dataType)
	override def withDescription(description: String) = copy(description = description)
	override def withIsArray(isArray: UncertainBoolean) = copy(isArray = isArray)
	override def withIsOptional(isOptional: Boolean) = copy(isOptional = isOptional)
	override def withIsSensitive(isSensitive: Boolean) = copy(isSensitive = isSensitive)
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
	override def withName(name: String) = copy(name = name)
	
	
	// OTHER    ---------------------------
	
	def ~==(update: NewProperty) =
		isArray == update.isArray && isOptional == update.isOptional && dataType == update.primaryType
	/**
	 * Checks whether this schema conflicts with the specified property update.
	 * Assumes that property names are an exact match.
	 * NB: Doesn't check referenced model types or alternative data types, since those are not
	 * included in this model.
	 */
	def !~==(update: NewProperty) = !(this ~== update)
}

