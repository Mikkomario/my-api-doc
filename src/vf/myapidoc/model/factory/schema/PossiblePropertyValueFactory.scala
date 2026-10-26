package vf.myapidoc.model.factory.schema

import java.time.Instant

/**
  * Common trait for possible property value-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
trait PossiblePropertyValueFactory[+A]
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
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	
	/**
	  * @param propId New prop id to assign
	  * @return Copy of this item with the specified prop id
	  */
	def withPropId(propId: Int): A
	
	/**
	  * @param value New value to assign
	  * @return Copy of this item with the specified value
	  */
	def withValue(value: String): A
}

