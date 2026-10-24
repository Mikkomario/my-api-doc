package vf.myapidoc.database.access.schema.prop.types

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing individual alternative property type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAlternativePropertyTypeValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing alternative property type database properties
	  */
	val model = AlternativePropertyTypeDbModel
	
	/**
	  * Access to alternative property type id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the property that has this alternative type
	  */
	lazy val propId = apply(model.propId).optional { v => v.int }
	
	/**
	  * Alternative data type supported by the linked property
	  */
	lazy val altType = apply(model.altType).optional { v => PropertyType.findForValue(v) }
	
	/**
	  * Time when this alternative property type was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this alternative property type became deprecated. None while this alternative 
	  * property type is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

