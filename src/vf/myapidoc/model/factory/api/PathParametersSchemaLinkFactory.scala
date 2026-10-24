package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for path parameters schema link-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PathParametersSchemaLinkFactory[+A]
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
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	
	/**
	  * @param nodeId New node id to assign
	  * @return Copy of this item with the specified node id
	  */
	def withNodeId(nodeId: Int): A
}

