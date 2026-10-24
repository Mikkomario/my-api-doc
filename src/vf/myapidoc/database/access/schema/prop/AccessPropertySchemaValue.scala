package vf.myapidoc.database.access.schema.prop

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.schema.PropertySchemaDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing individual property schema values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPropertySchemaValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing property schema database properties
	  */
	val model = PropertySchemaDbModel
	
	/**
	  * Access to property schema id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the model type that this property belongs to.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	
	/**
	  * Name of this property, as it should appear in the input or output model / JSON object.
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * This property's expected value type.
	  */
	lazy val dataType = apply(model.dataType).optional { v => PropertyType.findForValue(v) }
	
	/**
	  * If this property contains a model/object, this is the ID of the contained model's type. 
	  * None if this property uses some other data type.
	  */
	lazy val contentTypeId = apply(model.contentTypeId).optional { v => v.int }
	
	/**
	  * A description of this property's role/function.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	
	/**
	  * Time when this property schema was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this property schema became deprecated. None while this property schema is still 
	  * valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
	
	/**
	  * Whether this property is an array of values. False if this property contains a single value.
	  */
	lazy val isArray = apply(model.isArray).optional { v => v.boolean }
	
	/**
	  * Whether this property may be omitted from the input and/or output.
	  */
	lazy val isOptional = apply(model.isOptional).optional { v => v.boolean }
}

