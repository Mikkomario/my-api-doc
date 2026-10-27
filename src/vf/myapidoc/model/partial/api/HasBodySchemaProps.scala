package vf.myapidoc.model.partial.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for classes which provide access to body schema properties
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait HasBodySchemaProps extends ModelConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * ID of the element that contains this body.
	  */
	def parentId: Int
	
	/**
	  * Type of this body's contents.
	  */
	def dataType: PropertyType
	
	/**
	  * If this body contains an object or objects, contains the ID of the model schema that 
	  * describes the applicable object structure.
	  */
	def modelTypeId: Option[Int]
	
	/**
	  * Time when this body schema was added to the database
	  */
	def created: Instant
	
	/**
	  * Time when this body schema became deprecated. None while this body schema is still valid.
	  */
	def deprecatedAfter: Option[Instant]
	
	/**
	  * Whether this body contains an array of values. False if no more than one value may be 
	  * included.
	  */
	def isArray: Boolean
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("parentId" -> parentId, "dataType" -> dataType.id, "modelTypeId" -> modelTypeId, 
			"created" -> created, "deprecatedAfter" -> deprecatedAfter, "isArray" -> isArray))
}

