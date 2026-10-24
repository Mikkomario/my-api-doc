package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType}
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.schema.AlternativePropertyTypeFactory

import java.time.Instant

object AlternativePropertyTypeData extends FromModelFactory[AlternativePropertyTypeData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("propId", IntType, Single("prop_id")),
		PropertyDeclaration("altType", IntType, Single("alt_type")),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: HasProperties) = schema.validate(model).map { valid =>
		AlternativePropertyTypeData(valid("propId").getInt, PropertyType.fromValue(valid("altType")),
			valid("created").getInstant, valid("deprecatedAfter").instant)
	}
}

/**
  * Defines an alternative supported data type for a property.
  * @param propId          ID of the property that has this alternative type
  * @param altType         Alternative data type supported by the linked property
  * @param created         Time when this alternative property type was added to the database
  * @param deprecatedAfter Time when this alternative property type became deprecated. None while 
  *                        this alternative property type is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AlternativePropertyTypeData(propId: Int, altType: PropertyType, created: Instant = Now,
                                       deprecatedAfter: Option[Instant] = None)
	extends AlternativePropertyTypeFactory[AlternativePropertyTypeData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this alternative property type has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this alternative property type is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("propId" -> propId, "altType" -> altType.id, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter))
	
	override def withAltType(altType: PropertyType) = copy(altType = altType)
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withPropId(propId: Int) = copy(propId = propId)
}

