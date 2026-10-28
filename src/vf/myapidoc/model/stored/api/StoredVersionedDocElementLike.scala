package vf.myapidoc.model.stored.api

import utopia.flow.util.Mutate
import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.factory.api.{VersionedDocElementFactory, VersionedDocElementFactoryWrapper}
import vf.myapidoc.model.partial.api.VersionedDocElementDataLike

/**
  * Common trait for versioned doc elements that have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait StoredVersionedDocElementLike[Data <: VersionedDocElementDataLike[Data], +Repr]
	extends Stored[Data, Int] with StoredModelConvertible[Data] with FromIdFactory[Int, Repr] 
		with VersionedDocElementFactory[Repr] with VersionedDocElementDataLike[Repr]
		with VersionedDocElementFactoryWrapper[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def originalUpdateId = data.originalUpdateId
	override def removingUpdateId = data.removingUpdateId
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) =
		wrap(data.copyVersionedDocElement(originalUpdateId, removingUpdateId))
	
	override protected def mapWrapped(f: Mutate[Data]) = super[VersionedDocElementFactoryWrapper].mapWrapped(f)
}

