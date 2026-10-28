package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.DocUpdateDbModel

/**
  * Used for accessing individual doc update values from the DB
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class AccessDocUpdateValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing doc update database properties
	  */
	val model = DocUpdateDbModel
	
	/**
	  * Access to doc update id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the updated API
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	
	/**
	  * Time when this doc update was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
}

