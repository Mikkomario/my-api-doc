package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing individual body schema values from the DB
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait AccessBodySchemaValue extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * ID of the element that contains this body.
	  */
	lazy val parentId = apply(model.parentId).optional { v => v.int }
	/**
	  * Type of this body's contents.
	  */
	lazy val dataType = apply(model.dataType).optional { v => PropertyType.findForValue(v) }
	/**
	  * If this body contains an object or objects, contains the ID of the model schema that 
	  * describes the applicable object structure.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	/**
	  * Whether this body contains an array of values. False if no more than one value may be 
	  * included.
	  */
	lazy val isArray = apply(model.isArray).optional { v => v.boolean }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing body schema database properties
	  */
	def model: BodySchemaDbProps
}

