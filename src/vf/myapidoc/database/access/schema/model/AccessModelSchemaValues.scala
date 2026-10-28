package vf.myapidoc.database.access.schema.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel

/**
  * Used for accessing model schema values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessModelSchemaValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing model schema database properties
	  */
	override val model = ModelSchemaDbModel
	
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
	
	
	// COMPUTED	--------------------
	
	/**
	  * A map of accessible model types, where keys are model schema IDs and values are model type 
	  * names
	  * @param connection Implicit DB connection
	  */
	def idToName(implicit connection: Connection) = 
		access.toMap(model.id, model.name) { _.getInt } { _.getString }
}

