package vf.myapidoc.model.factory.api

/**
  * Common trait for versioned doc element-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param originalUpdateId New original update id to assign
	  * @return Copy of this item with the specified original update id
	  */
	def withOriginalUpdateId(originalUpdateId: Int): A
	
	/**
	  * @param removingUpdateId New removing update id to assign
	  * @return Copy of this item with the specified removing update id
	  */
	def withRemovingUpdateId(removingUpdateId: Int): A
}

