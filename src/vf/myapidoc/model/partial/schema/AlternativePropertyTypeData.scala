package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.{FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType}
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.AnyProp
import vf.myapidoc.model.factory.schema.AlternativePropertyTypeFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}

import java.time.Instant

object AlternativePropertyTypeData 
	extends FromModelFactoryWithSchema[AlternativePropertyTypeData] 
		with FromModelFactory[AlternativePropertyTypeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("propId", IntType, Single("prop_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("altType", IntType, Single("alt_type"), AnyProp.id)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AlternativePropertyTypeData(valid("originalUpdateId").getInt, valid("propId").getInt, 
			valid("removingUpdateId").int, PropertyType.fromValue(valid("altType")))
}

/**
  * Defines an alternative supported data type for a property.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param propId           ID of the property that has this alternative type
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param altType          Alternative data type supported by the linked property
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AlternativePropertyTypeData(originalUpdateId: Int, propId: Int, removingUpdateId: Option[Int] = None,
                                       altType: PropertyType = AnyProp)
	extends AlternativePropertyTypeFactory[AlternativePropertyTypeData] with VersionedDocElementData 
		with VersionedDocElementDataLike[AlternativePropertyTypeData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Pair("propId" -> propId, "altType" -> altType.id))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withAltType(altType: PropertyType) = copy(altType = altType)
	override def withPropId(propId: Int) = copy(propId = propId)
}

