package vf.myapidoc.database.access.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.ApiDbModel

/**
  * Used for accessing api values from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class AccessApiValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api database properties
	  */
	val model = ApiDbModel
	
	/**
	  * Access to api ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * (Human readable) name of this API
	  */
	lazy val names = apply(model.name) { v => v.getString }
	
	/**
	  * Time when this api was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
}

