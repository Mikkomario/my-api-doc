package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for doc update-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait DocUpdateFactory[+A]
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
}

