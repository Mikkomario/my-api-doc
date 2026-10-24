package vf.myapidoc.database.access.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.ApiDbModel

/**
  * Used for accessing individual api values from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class AccessApiValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api database properties
	  */
	val model = ApiDbModel
	
	/**
	  * Access to api id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * (Human readable) name of this API
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * Time when this api was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
}

