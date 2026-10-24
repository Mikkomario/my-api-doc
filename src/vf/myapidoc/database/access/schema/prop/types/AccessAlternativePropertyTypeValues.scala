package vf.myapidoc.database.access.schema.prop.types

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing alternative property type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAlternativePropertyTypeValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing alternative property type database properties
	  */
	val model = AlternativePropertyTypeDbModel
	
	/**
	  * Access to alternative property type ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the property that has this alternative type
	  */
	lazy val propIds = apply(model.propId) { v => v.getInt }
	/**
	  * Alternative data type supported by the linked property
	  */
	lazy val altTypes = apply(model.altType)(PropertyType.fromValue)
	/**
	  * Time when this alternative property type was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this alternative property type became deprecated. None while this alternative 
	  * property type is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	
	
	// COMPUTED -------------------------
	
	/**
	 * @param connection Implicit DB connection
	 * @return A map where keys are property IDs and values are alternative types defined for those properties
	 */
	def toTypeMap(implicit connection: Connection) =
		access.toMultiMap(model.propId, model.altType, includeDefault = true) {
			_.getInt } { _.view.map(PropertyType.fromValue).toSet }
}

