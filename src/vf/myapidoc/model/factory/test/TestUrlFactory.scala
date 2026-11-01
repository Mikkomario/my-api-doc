package vf.myapidoc.model.factory.test

import java.time.Instant

/**
  * Common trait for test url-related factories that allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
trait TestUrlFactory[+A]
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
	  * @param rootUrl New root url to assign
	  * @return Copy of this item with the specified root url
	  */
	def withRootUrl(rootUrl: String): A
}

