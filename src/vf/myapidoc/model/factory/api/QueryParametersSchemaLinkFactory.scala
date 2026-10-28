package vf.myapidoc.model.factory.api

/**
  * Common trait for query parameters schema link-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait QueryParametersSchemaLinkFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
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

