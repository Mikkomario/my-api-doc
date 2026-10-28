package vf.myapidoc.database.access.schema.prop.types

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing individual alternative property type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAlternativePropertyTypeValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing alternative property type database properties
	  */
	override val model = AlternativePropertyTypeDbModel
	
	/**
	  * ID of the property that has this alternative type
	  */
	lazy val propId = apply(model.propId).optional { v => v.int }
	/**
	  * Alternative data type supported by the linked property
	  */
	lazy val altType = apply(model.altType).optional { v => PropertyType.findForValue(v) }
}

