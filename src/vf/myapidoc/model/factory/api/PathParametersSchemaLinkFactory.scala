package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for path parameters schema link-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PathParametersSchemaLinkFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
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

