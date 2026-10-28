package vf.myapidoc.model.enumeration

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible
import utopia.flow.operator.equality.EqualsExtensions._

import java.util.NoSuchElementException

/**
  * An enumeration for different token use types
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
sealed trait TokenUseType extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this token use type in database and json
	  */
	def id: Int
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
}

object TokenUseType
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available token use type values
	  */
	val values: Pair[TokenUseType] = Pair(Bearer, Basic)
	
	
	// COMPUTED	--------------------
	
	/**
	 * The default token use type (i.e. bearer)
	 */
	def default = Bearer
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a token use type
	  * @return token use type matching the specified id. None if the id didn't match any token use type
	  */
	def findForId(id: Int) = values.find { _.id == id }
	/**
	  * @param value A value representing an token use type id
	  * @return token use type matching the specified value. None if the value didn't match any token use type
	  */
	def findForValue(value: Value) = value.intOrString.flatMap {
		case Left(id) => findForId(id); case Right(name) => values.find { _.toString ~== name }
	}
	/**
	 * @param id id matching a token use type
	 * @return token use type matching that id, or the default token use type (bearer)
	 */
	def forId(id: Int) = findForId(id).getOrElse(default)
	/**
	 * @param value A value representing a token use type id
	 * @return token use type matching the specified value, when the value is interpreted as an token use
	 * type id, or the default token use type (bearer)
	 */
	def fromValue(value: Value) = findForValue(value).getOrElse(default)
	
	
	// NESTED	--------------------
	
	/**
	  * Represents the basic authentication use-case, which combines username and password.
	  * @since 15.10.2026
	  */
	case object Basic extends TokenUseType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
	}
	
	/**
	  * Represents a case where the token is used for bearer authentication
	  * @since 15.10.2026
	  */
	case object Bearer extends TokenUseType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
	}
}

