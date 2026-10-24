package vf.myapidoc.model.partial.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

import java.time.Instant

/**
  * Common trait for classes which provide access to api node method implementation model schema 
  * link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait HasApiNodeMethodImplementationModelSchemaLinkProps extends ModelConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * ID of the method implementation that uses the linked schema.
	  */
	def implementationId: Int
	
	/**
	  * ID of the schema that defines the type of model accepted by the linked implementation.
	  */
	def modelTypeId: Int
	
	/**
	  * Time when this api node method implementation model schema link was added to the database
	  */
	def created: Instant
	
	/**
	  * Time when this api node method implementation model schema link became deprecated. None while 
	  * this api node method implementation model schema link is still valid.
	  */
	def deprecatedAfter: Option[Instant]
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("implementationId" -> implementationId, "modelTypeId" -> modelTypeId, 
			"created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

