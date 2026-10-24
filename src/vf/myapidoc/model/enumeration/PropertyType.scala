package vf.myapidoc.model.enumeration

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible
import utopia.flow.operator.equality.EqualsExtensions._

import java.util.NoSuchElementException

/**
  * An enumeration for different data types in input and output models
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
sealed trait PropertyType extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this property type in database and json
	  */
	def id: Int
	
	/**
	  * Name of this type
	  */
	def name: String
	
	
	// IMPLEMENTED	--------------------
	
	override def toString = name
	
	override def toValue = id
}

object PropertyType
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available property type values
	  */
	val values: Vector[PropertyType] = 
		Vector(AnyProp, StringProp, IntProp, DecimalProp, LongProp, BooleanProp, InstantProp, DurationProp, 
			ModelProp)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a property type
	  * @return property type matching the specified id. None if the id didn't match any property type
	  */
	def findForId(id: Int) = values.find { _.id == id }
	def findForName(name: String) = values.find { _.name ~== name }
	
	/**
	  * @param value A value representing a property type id
	  * @return property type matching the specified value. None if the value didn't match any property type
	  */
	def findForValue(value: Value) = value.intOrString.flatMap {
		case Left(id) => findForId(id)
		case Right(name) => findForName(name)
	}
	
	/**
	  * @param id id matching a property type
	  * @return property type matching that id. Any if no matching value was found.
	  */
	def forId(id: Int) = findForId(id).getOrElse(AnyProp)
	/**
	  * @param value A value representing a property type id
	  * @return property type matching the specified value, when the value is interpreted as a property type
	  * id. Any if no matching value was found.
	  */
	def fromValue(value: Value) = findForValue(value).getOrElse(AnyProp)
	
	
	// NESTED	--------------------
	
	/**
	  * An undefined property type. Used when the type is not known or doesn't match any other type.
	  * @since 21.10.2026
	  */
	case object AnyProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		override val name: String = "Any"
	}
	
	/**
	  * A boolean property type. Transmitted in JSON as `true` or `false`.
	  * @since 15.10.2026
	  */
	case object BooleanProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 6
		override val name: String = "Boolean"
	}
	
	/**
	  * A property type for decimal numbers: Double and Float.
	  * @since 15.10.2026
	  */
	case object DecimalProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 4
		override val name: String = "Double"
	}
	
	/**
	  * A property for representing durations. Transmitted in JSON by coupling amount and unit, e.g. 
	  * "48 seconds" or "2min"
	  * @since 15.10.2026
	  */
	case object DurationProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 8
		override val name: String = "Duration"
	}
	
	/**
	  * A timestamp property type. Transmitted in JSON as ISO 8601 strings.
	  * @since 15.10.2026
	  */
	case object InstantProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 7
		override val name: String = "Instant"
	}
	
	case object IntProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
		override val name: String = "Int"
	}
	
	case object LongProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 5
		override val name: String = "Long"
	}
	
	/**
	  * A JSON object type. Usually coupled with a model type reference.
	  * @since 15.10.2026
	  */
	case object ModelProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 9
		override val name: String = "Object"
	}
	
	case object StringProp extends PropertyType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
		override val name: String = "String"
	}
}

