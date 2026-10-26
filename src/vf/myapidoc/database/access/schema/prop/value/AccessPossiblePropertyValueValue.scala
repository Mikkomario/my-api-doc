package vf.myapidoc.database.access.schema.prop.value

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.schema.PossiblePropertyValueDbModel

/**
  * Used for accessing individual possible property value values from the DB
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class AccessPossiblePropertyValueValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing possible property value database properties
	  */
	val model = PossiblePropertyValueDbModel
	
	/**
	  * Access to possible property value id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the property that this value may be assigned to
	  */
	lazy val propId = apply(model.propId).optional { v => v.int }
	
	/**
	  * The value the linked property may have
	  */
	lazy val value = apply(model.value) { v => v.getString }
	
	/**
	  * Description of this value's role / function
	  */
	lazy val description = apply(model.description) { v => v.getString }
	
	/**
	  * Time when this possible property value was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this possible property value became deprecated. None while this possible property 
	  * value is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

