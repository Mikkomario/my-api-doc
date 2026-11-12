package vf.myapidoc.model.partial.example

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration, Value}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.example.UndocumentedPropertyFactory

object UndocumentedPropertyData extends FromModelFactoryWithSchema[UndocumentedPropertyData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("responseId", IntType, Single("response_id")), 
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")), 
		PropertyDeclaration("name", StringType), 
		PropertyDeclaration("value", AnyType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model) = 
		UndocumentedPropertyData(responseId = model("responseId").getInt, 
			modelTypeId = model("modelTypeId").getInt, name = model("name").getString, value = model("value"))
}

/**
  * Documents a property received from a tested API that didn't appear in the documentation
  * @param responseId  ID of the response that contained this property
  * @param modelTypeId ID of the model schema that missed this property
  * @param name        Name of the received property
  * @param value       Received property value
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class UndocumentedPropertyData(responseId: Int, modelTypeId: Int, name: String, value: Value = Value.empty)
	extends UndocumentedPropertyFactory[UndocumentedPropertyData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("responseId" -> responseId, "modelTypeId" -> modelTypeId, "name" -> name,
		"value" -> value))
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
	override def withName(name: String) = copy(name = name)
	override def withResponseId(responseId: Int) = copy(responseId = responseId)
	override def withValue(value: Value) = copy(value = value)
}

