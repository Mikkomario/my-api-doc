package vf.myapidoc.model.factory.api

import utopia.access.model.enumeration.Method

import java.time.Instant

/**
  * Common trait for api node method implementation-related factories that allow construction 
  * with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	/**
	  * @param method New method to assign
	  * @return Copy of this item with the specified method
	  */
	def withMethod(method: Method): A
	/**
	  * @param nodeId New node id to assign
	  * @return Copy of this item with the specified node id
	  */
	def withNodeId(nodeId: Int): A
	/**
	  * @param requiresRequestBody New requires request body to assign
	  * @return Copy of this item with the specified requires request body
	  */
	def withRequiresRequestBody(requiresRequestBody: Boolean): A
}

