package vf.myapidoc.model.factory.example

import java.nio.file.Path
import java.time.Instant

/**
  * Common trait for example response-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait ExampleResponseFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param relativeFilePath New relative file path to assign
	  * @return Copy of this item with the specified relative file path
	  */
	def withRelativeFilePath(relativeFilePath: Path): A
	
	/**
	  * @param statusId New status id to assign
	  * @return Copy of this item with the specified status id
	  */
	def withStatusId(statusId: Int): A
	
	/**
	  * @param testedApiVersionId New tested api version id to assign
	  * @return Copy of this item with the specified tested api version id
	  */
	def withTestedApiVersionId(testedApiVersionId: Int): A
}

