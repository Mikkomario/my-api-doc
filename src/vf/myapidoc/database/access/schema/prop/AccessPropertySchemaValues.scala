package vf.myapidoc.database.access.schema.prop

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.util.UncertainBoolean
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.schema.PropertySchemaDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing property schema values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPropertySchemaValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing property schema database properties
	  */
	val model = PropertySchemaDbModel
	
	/**
	  * Access to property schema ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the model type that this property belongs to.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId) { v => v.getInt }
	/**
	  * Name of this property, as it should appear in the input or output model / JSON object.
	  */
	lazy val names = apply(model.name) { v => v.getString }
	/**
	  * This property's expected value type.
	  */
	lazy val dataTypes = apply(model.dataType)(PropertyType.fromValue)
	/**
	  * If this property contains a model/object, this is the ID of the contained model's type. 
	  * None if this property uses some other data type.
	  */
	lazy val contentTypeIds = apply(model.contentTypeId).flatten { v => v.int }
	/**
	  * A description of this property's role/function.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	/**
	  * Time when this property schema was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this property schema became deprecated. None while this property schema is still 
	  * valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	/**
	  * Whether this property is an array of values. False if this property contains a single value.
	  */
	lazy val areArrays = apply(model.isArray) { v => UncertainBoolean(v.boolean) }
	/**
	  * Whether this property may be omitted from the input and/or output.
	  */
	lazy val areOptional = apply(model.isOptional) { v => v.getBoolean }
}

