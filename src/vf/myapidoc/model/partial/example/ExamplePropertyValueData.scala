package vf.myapidoc.model.partial.example

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration, Value}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.example.ExamplePropertyValueFactory

object ExamplePropertyValueData extends FromModelFactoryWithSchema[ExamplePropertyValueData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("responseId", IntType, Single("response_id")), 
		PropertyDeclaration("propId", IntType, Single("prop_id")), 
		PropertyDeclaration("value", AnyType, isOptional = true), 
		PropertyDeclaration("matchedDocumentation", BooleanType, Single("matched_documentation"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model) = 
		ExamplePropertyValueData(responseId = model("responseId").getInt, propId = model("propId").getInt, 
			value = model("value"), matchedDocumentation = model("matchedDocumentation").getBoolean)
}

/**
  * Documents a single property value received from a tested API
  * @param responseId           ID of the response that contained this value
  * @param propId               ID of the property schema that matches the received property
  * @param value                Received property value
  * @param matchedDocumentation Whether the received value matches the documented property
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExamplePropertyValueData(responseId: Int, propId: Int, value: Value = Value.empty,
                                    matchedDocumentation: Boolean = false)
	extends ExamplePropertyValueFactory[ExamplePropertyValueData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("responseId" -> responseId, "propId" -> propId, "value" -> value,
		"matchedDocumentation" -> matchedDocumentation))
	
	override def withMatchedDocumentation(matchedDocumentation: Boolean) = 
		copy(matchedDocumentation = matchedDocumentation)
	override def withPropId(propId: Int) = copy(propId = propId)
	override def withResponseId(responseId: Int) = copy(responseId = responseId)
	override def withValue(value: Value) = copy(value = value)
}

