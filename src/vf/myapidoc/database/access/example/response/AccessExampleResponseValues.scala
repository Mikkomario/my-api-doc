package vf.myapidoc.database.access.example.response

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.example.ExampleResponseDbModel

import java.nio.file.Path

/**
  * Used for accessing example response values from the DB
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExampleResponseValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing example response database properties
	  */
	val model = ExampleResponseDbModel
	
	/**
	  * Access to example response ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the tested API (document) version
	  */
	lazy val testedApiVersionIds = apply(model.testedApiVersionId) { v => v.getInt }
	/**
	  * ID of the response (status) documentation entry that matches the received response
	  */
	lazy val statusIds = apply(model.statusId) { v => v.getInt }
	/**
	  * Path to the file where the response body is stored. Relative to the examples directory.
	  */
	lazy val relativeFilePaths = apply(model.relativeFilePath) { v => v.getString: Path } { _.toJson }
	/**
	  * Time when this example response was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
}

