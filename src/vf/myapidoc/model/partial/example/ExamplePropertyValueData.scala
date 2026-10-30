package vf.myapidoc.model.partial.example

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration, Value}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.example.ExamplePropertyValueFactory

object ExamplePropertyValueData extends FromModelFactoryWithSchema[ExamplePropertyValueData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(
		PropertyDeclaration("responseId", IntType, Single("response_id")), 
		PropertyDeclaration("propId", IntType, Single("prop_id")), 
		PropertyDeclaration("value", AnyType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ExamplePropertyValueData(valid("responseId").getInt, valid("propId").getInt, valid("value"))
}

/**
  * Documents a single property value received from a tested API
  * @param responseId ID of the response that contained this value
  * @param propId     ID of the property schema that matches the received property
  * @param value      Received property value
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExamplePropertyValueData(responseId: Int, propId: Int, value: Value = Value.empty) 
	extends ExamplePropertyValueFactory[ExamplePropertyValueData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("responseId" -> responseId, "propId" -> propId, "value" -> value))
	
	override def withPropId(propId: Int) = copy(propId = propId)
	
	override def withResponseId(responseId: Int) = copy(responseId = responseId)
	
	override def withValue(value: Value) = copy(value = value)
}

