package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for api node group-related factories which allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNodeGroupFactory[+A]
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
	  * @param deprecatedAfter New deprecated after to assign
	  * @return Copy of this item with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant): A
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

