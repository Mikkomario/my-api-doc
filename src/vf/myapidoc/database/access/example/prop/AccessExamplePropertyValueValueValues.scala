package vf.myapidoc.database.access.example.prop

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.example.ExamplePropertyValueDbModel
import vf.myapidoc.util.Common.jsonParser

/**
  * Used for accessing example property value values from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExamplePropertyValueValueValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing example property value database properties
	  */
	val model = ExamplePropertyValueDbModel
	
	/**
	  * Access to example property value ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the response that contained this value
	  */
	lazy val responseIds = apply(model.responseId) { v => v.getInt }
	/**
	  * ID of the property schema that matches the received property
	  */
	lazy val propIds = apply(model.propId) { v => v.getInt }
	/**
	  * Received property value
	  */
	lazy val values = apply(model.value) { v => v.mapIfNotEmpty { v => jsonParser.valueOf(v.getString) } }
	
	
	// COMPUTED ----------------------------
	
	/**
	 * @param connection Implicit DB connection
	 * @return All accessible example values as a map where keys are property IDs and values are example values
	 *         (many-to-one)
	 */
	def valuesPerProp(implicit connection: Connection) =
		access.toMultiMap(model.propId, model.value, includeDefault = true) { _.getInt } {
			_.map { _.mapIfNotEmpty { v => jsonParser.valueOf(v.getString) } }
		}
}

