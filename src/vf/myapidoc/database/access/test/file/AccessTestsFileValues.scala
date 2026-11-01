package vf.myapidoc.database.access.test.file

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.test.TestsFileDbModel

import java.nio.file.Path

/**
  * Used for accessing tests file values from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestsFileValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing tests file database properties
	  */
	val model = TestsFileDbModel
	
	/**
	  * Access to tests file ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the tested API
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	/**
	  * Path to the tests-file
	  */
	lazy val paths = apply(model.path) { v => v.getString: Path } { _.toJson }
	/**
	  * Time when this tests file was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
}

