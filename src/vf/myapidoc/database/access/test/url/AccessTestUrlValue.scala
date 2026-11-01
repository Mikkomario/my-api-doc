package vf.myapidoc.database.access.test.url

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.test.TestUrlDbModel

/**
  * Used for accessing individual test url values from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestUrlValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing test url database properties
	  */
	val model = TestUrlDbModel
	
	/**
	  * Access to test url id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the targeted API
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	
	/**
	  * Name given to this URL. May be empty.
	  */
	lazy val name = apply(model.name) { v => v.getString }
	
	/**
	  * URL to the root API node. E.g. "https://domain/api/v1"
	  */
	lazy val rootUrl = apply(model.rootUrl) { v => v.getString }
	
	/**
	  * Time when this test url was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
}

