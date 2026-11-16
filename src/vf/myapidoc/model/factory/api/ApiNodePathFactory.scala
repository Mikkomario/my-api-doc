package vf.myapidoc.model.factory.api

/**
  * Common trait for api node path-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait ApiNodePathFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param nodeId New node id to assign
	  * @return Copy of this item with the specified node id
	  */
	def withNodeId(nodeId: Int): A
	
	/**
	  * @param paramsModelSchemaId New params model schema id to assign
	  * @return Copy of this item with the specified params model schema id
	  */
	def withParamsModelSchemaId(paramsModelSchemaId: Int): A
	
	/**
	  * @param relative New relative to assign
	  * @return Copy of this item with the specified relative
	  */
	def withRelative(relative: String): A
}

