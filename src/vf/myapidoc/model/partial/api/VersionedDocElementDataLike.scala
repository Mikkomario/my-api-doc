package vf.myapidoc.model.partial.api

import vf.myapidoc.model.factory.api.VersionedDocElementFactory

/**
  * Common trait for classes that provide read and copy access to versioned doc element properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDataLike[+Repr] 
	extends HasVersionedDocElementProps with VersionedDocElementFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this versioned doc element
	  * @param originalUpdateId New original update id to assign. Default = current value.
	  * @param removingUpdateId New removing update id to assign. Default = current value.
	  * @return A copy of this versioned doc element with the specified properties
	  */
	def copyVersionedDocElement(originalUpdateId: Int = originalUpdateId, 
		removingUpdateId: Option[Int] = removingUpdateId): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withOriginalUpdateId(originalUpdateId: Int) = 
		copyVersionedDocElement(originalUpdateId = originalUpdateId)
	
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		copyVersionedDocElement(removingUpdateId = Some(removingUpdateId))
}

