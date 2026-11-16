package vf.myapidoc.model.factory.api

import utopia.vault.store.CanCopyData

/**
  * Common trait for classes that implement VersionedDocElementFactory by wrapping another such 
  * instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait VersionedDocElementFactoryWrapper[Data <: VersionedDocElementFactory[Data], +Repr] 
	extends VersionedDocElementFactory[Repr] with CanCopyData[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withOriginalUpdateId(originalUpdateId: Int) = 
		mapData { _.withOriginalUpdateId(originalUpdateId) }
	
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		mapData { _.withRemovingUpdateId(removingUpdateId) }
}

