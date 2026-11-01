package vf.myapidoc.database.access.test.url

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.test.TestUrlDbModel

/**
  * Used for accessing test url values from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestUrlValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing test url database properties
	  */
	val model = TestUrlDbModel
	
	/**
	  * Access to test url ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * ID of the targeted API
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	
	/**
	  * Name given to this URL. May be empty.
	  */
	lazy val names = apply(model.name) { v => v.getString }
	
	/**
	  * URL to the root API node. E.g. "https://domain/api/v1"
	  */
	lazy val rootUrls = apply(model.rootUrl) { v => v.getString }
	
	/**
	  * Time when this test url was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
}

