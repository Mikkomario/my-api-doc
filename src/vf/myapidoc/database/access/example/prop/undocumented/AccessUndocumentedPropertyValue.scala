package vf.myapidoc.database.access.example.prop.undocumented

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.example.UndocumentedPropertyDbModel

/**
  * Used for accessing individual undocumented property values from the DB
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class AccessUndocumentedPropertyValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing undocumented property database properties
	  */
	val model = UndocumentedPropertyDbModel
	
	/**
	  * Access to undocumented property id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the response that contained this property
	  */
	lazy val responseId = apply(model.responseId).optional { v => v.int }
	
	/**
	  * ID of the model schema that missed this property
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	
	/**
	  * Name of the received property
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * Received property value
	  */
	lazy val value = apply(model.value) { v => v.mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) } }
}

