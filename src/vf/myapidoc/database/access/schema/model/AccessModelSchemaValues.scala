package vf.myapidoc.database.access.schema.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel

/**
  * Used for accessing model schema values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessModelSchemaValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing model schema database properties
	  */
	val model = ModelSchemaDbModel
	
	/**
	  * Access to model schema ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the API this model type belongs to.
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	/**
	  * Name of this model type (/class). Empty if unnamed. 
	  * Named model types are often referenced from multiple sources, while unnamed types are 
	  * typically single-use.
	  */
	lazy val names = apply(model.name) { v => v.getString }
	/**
	  * Description of this model type's role / function.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	/**
	  * Time when this model schema was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	/**
	  * Time when this model schema became deprecated. None while this model schema is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	
	
	// COMPUTED -------------------------
	
	/**
	 * @param connection Implicit DB connection
	 * @return A map of accessible model types, where keys are model schema IDs and values are model type names
	 */
	def idToName(implicit connection: Connection) =
		access.toMap(model.id, model.name) { _.getInt } { _.getString }
}

