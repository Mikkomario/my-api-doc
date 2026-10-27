package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessValues
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing body schema values from the DB
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait AccessBodySchemaValues extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Access to body schema ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the element that contains this body.
	  */
	lazy val parentIds = apply(model.parentId) { v => v.getInt }
	/**
	  * Type of this body's contents.
	  */
	lazy val dataTypes = apply(model.dataType)(PropertyType.fromValue)
	/**
	  * If this body contains an object or objects, contains the ID of the model schema that 
	  * describes the applicable object structure.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId).flatten { v => v.int }
	/**
	  * Time when this body schema was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this body schema became deprecated. None while this body schema is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	/**
	  * Whether this body contains an array of values. False if no more than one value may be 
	  * included.
	  */
	lazy val areArrays = apply(model.isArray) { v => v.getBoolean }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing body schema database properties
	  */
	def model: BodySchemaDbProps
}

