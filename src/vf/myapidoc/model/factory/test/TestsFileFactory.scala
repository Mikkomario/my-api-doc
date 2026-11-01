package vf.myapidoc.model.factory.test

import java.nio.file.Path
import java.time.Instant

/**
  * Common trait for tests file-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
trait TestsFileFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param apiId New api id to assign
	  * @return Copy of this item with the specified api id
	  */
	def withApiId(apiId: Int): A
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param path New path to assign
	  * @return Copy of this item with the specified path
	  */
	def withPath(path: Path): A
}

