package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.schema.PossiblePropertyValueFactory

import java.time.Instant

object PossiblePropertyValueData extends FromModelFactoryWithSchema[PossiblePropertyValueData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("propId", IntType, Single("prop_id")), 
			PropertyDeclaration("value", StringType), PropertyDeclaration("description", StringType, 
			isOptional = true), PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PossiblePropertyValueData(valid("propId").getInt, valid("value").getString, 
			valid("description").getString, valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Defines a possible value for an enum property
  * @param propId          ID of the property that this value may be assigned to
  * @param value           The value the linked property may have
  * @param description     Description of this value's role / function
  * @param created         Time when this possible property value was added to the database
  * @param deprecatedAfter Time when this possible property value became deprecated. None while 
  *                        this possible property value is still valid.
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class PossiblePropertyValueData(propId: Int, value: String, description: String = "", 
	created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
	extends PossiblePropertyValueFactory[PossiblePropertyValueData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this possible property value has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this possible property value is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("propId" -> propId, "value" -> value, "description" -> description, 
			"created" -> created, "deprecatedAfter" -> deprecatedAfter))
	
	override def withCreated(created: Instant) = copy(created = created)
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withPropId(propId: Int) = copy(propId = propId)
	
	override def withValue(value: String) = copy(value = value)
}

