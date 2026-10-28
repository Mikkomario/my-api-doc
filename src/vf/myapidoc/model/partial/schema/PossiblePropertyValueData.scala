package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.schema.PossiblePropertyValueFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}

import java.time.Instant

object PossiblePropertyValueData extends FromModelFactoryWithSchema[PossiblePropertyValueData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("propId", IntType, Single("prop_id")),
		PropertyDeclaration("value", StringType),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PossiblePropertyValueData(valid("originalUpdateId").getInt, valid("propId").getInt, 
			valid("value").getString, valid("removingUpdateId").int, valid("description").getString)
}

/**
  * Defines a possible value for an enum property
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param propId           ID of the property that this value may be assigned to
  * @param value            The value the linked property may have
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param description      Description of this value's role / function
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class PossiblePropertyValueData(originalUpdateId: Int, propId: Int, value: String, 
	removingUpdateId: Option[Int] = None, description: String = "") 
	extends PossiblePropertyValueFactory[PossiblePropertyValueData] with VersionedDocElementData 
		with VersionedDocElementDataLike[PossiblePropertyValueData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("propId" -> propId, "value" -> value,
		"description" -> description))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDescription(description: String) = copy(description = description)
	override def withPropId(propId: Int) = copy(propId = propId)
	override def withValue(value: String) = copy(value = value)
}

