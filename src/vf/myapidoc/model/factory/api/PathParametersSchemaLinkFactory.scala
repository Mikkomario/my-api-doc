package vf.myapidoc.model.factory.api

/**
  * Common trait for path parameters schema link-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
@deprecated("Replaced with ApiNodePathFactory", "v0.1")
trait PathParametersSchemaLinkFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	/**
	  * @param pathId New path id to assign
	  * @return Copy of this item with the specified path id
	  */
	def withPathId(pathId: Int): A
}

