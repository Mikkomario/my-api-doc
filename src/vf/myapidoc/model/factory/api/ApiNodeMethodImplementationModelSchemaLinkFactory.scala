package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for api node method implementation model schema link-related factories which 
  * allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
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
	  * @param implementationId New implementation id to assign
	  * @return Copy of this item with the specified implementation id
	  */
	def withImplementationId(implementationId: Int): A
	
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
}

