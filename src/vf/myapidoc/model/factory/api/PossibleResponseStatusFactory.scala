package vf.myapidoc.model.factory.api

import utopia.flow.util.UncertainBoolean

import java.time.Instant

/**
  * Common trait for possible response status-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait PossibleResponseStatusFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	/**
	  * @param hasBody New has body to assign
	  * @return Copy of this item with the specified has body
	  */
	def withHasBody(hasBody: UncertainBoolean): A
	/**
	  * @param implementationId New implementation id to assign
	  * @return Copy of this item with the specified implementation id
	  */
	def withImplementationId(implementationId: Int): A
	/**
	  * @param statusCode New status code to assign
	  * @return Copy of this item with the specified status code
	  */
	def withStatusCode(statusCode: Int): A
}

