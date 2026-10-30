package vf.myapidoc.database.access.example.prop

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.example.ExamplePropertyValueDbModel

/**
  * Used for accessing individual example property value values from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExamplePropertyValueValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing example property value database properties
	  */
	val model = ExamplePropertyValueDbModel
	
	/**
	  * Access to example property value id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the response that contained this value
	  */
	lazy val responseId = apply(model.responseId).optional { v => v.int }
	
	/**
	  * ID of the property schema that matches the received property
	  */
	lazy val propId = apply(model.propId).optional { v => v.int }
	
	/**
	  * Received property value
	  */
	lazy val value = apply(model.value) { v => v.mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) } }
}

