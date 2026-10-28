package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.DocUpdateDbModel

/**
  * Used for accessing doc update values from the DB
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class AccessDocUpdateValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing doc update database properties
	  */
	val model = DocUpdateDbModel
	
	/**
	  * Access to doc update ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * ID of the updated API
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	
	/**
	  * Time when this doc update was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
}

