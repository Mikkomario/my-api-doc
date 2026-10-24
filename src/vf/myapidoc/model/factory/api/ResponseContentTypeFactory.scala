package vf.myapidoc.model.factory.api

import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for response content type-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ResponseContentTypeFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param contentType New content type to assign
	  * @return Copy of this item with the specified content type
	  */
	def withContentType(contentType: PropertyType): A
	
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
	  * @param isArray New is array to assign
	  * @return Copy of this item with the specified is array
	  */
	def withIsArray(isArray: Boolean): A
	
	/**
	  * @param isEmpty New is empty to assign
	  * @return Copy of this item with the specified is empty
	  */
	def withIsEmpty(isEmpty: Boolean): A
	
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	
	/**
	  * @param statusId New status id to assign
	  * @return Copy of this item with the specified status id
	  */
	def withStatusId(statusId: Int): A
}

