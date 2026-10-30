package vf.myapidoc.database.access.example.response

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.example.ExampleResponseDbModel

import java.nio.file.{Path, Paths}

/**
  * Used for accessing individual example response values from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExampleResponseValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing example response database properties
	  */
	val model = ExampleResponseDbModel
	
	/**
	  * Access to example response id
	  */
	lazy val id = apply(model.index).optional { _.int }
	/**
	  * ID of the tested API (document) version
	  */
	lazy val testedApiVersionId = apply(model.testedApiVersionId).optional { v => v.int }
	/**
	  * ID of the response (status) documentation entry that matches the received response
	  */
	lazy val statusId = apply(model.statusId).optional { v => v.int }
	/**
	  * Path to the file where the response body is stored. Relative to the examples directory.
	  */
	lazy val relativeFilePath = 
		apply(model.relativeFilePath).optional[Path] { v => v.string.map { Paths.get(_) } } { _.toJson }
	/**
	  * Time when this example response was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
}

