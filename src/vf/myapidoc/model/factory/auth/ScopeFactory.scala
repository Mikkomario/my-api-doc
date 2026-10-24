package vf.myapidoc.model.factory.auth

import java.time.Instant

/**
  * Common trait for scope-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ScopeFactory[+A]
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
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

