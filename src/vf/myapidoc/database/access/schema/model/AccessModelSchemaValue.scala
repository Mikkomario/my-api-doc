package vf.myapidoc.database.access.schema.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel

/**
  * Used for accessing individual model schema values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessModelSchemaValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing model schema database properties
	  */
	val model = ModelSchemaDbModel
	
	/**
	  * Access to model schema id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the API this model type belongs to.
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	
	/**
	  * Name of this model type (/class). Empty if unnamed. 
	  * Named model types are often referenced from multiple sources, while unnamed types are 
	  * typically single-use.
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * Description of this model type's role / function.
	  */
	lazy val description = apply(model.description) { v => v.getString }
	
	/**
	  * Time when this model schema was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this model schema became deprecated. None while this model schema is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

