package vf.myapidoc.database.access.test.file

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.test.TestsFileDbModel

import java.nio.file.Path

/**
  * Used for accessing individual tests file values from the DB
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestsFileValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing tests file database properties
	  */
	val model = TestsFileDbModel
	
	/**
	  * Access to tests file id
	  */
	lazy val id = apply(model.index).optional { _.int }
	/**
	  * ID of the tested API
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	/**
	  * Path to the tests-file
	  */
	lazy val path = apply(model.path).optional { v => v.string.map { v => v: Path } } { _.toJson }
	/**
	  * Time when this tests file was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
}

