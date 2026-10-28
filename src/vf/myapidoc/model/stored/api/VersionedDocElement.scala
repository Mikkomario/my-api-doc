package vf.myapidoc.model.stored.api

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.api.VersionedDocElementData

object VersionedDocElement extends StandardStoredFactory[VersionedDocElementData, VersionedDocElement]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = VersionedDocElementData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new versioned doc element
	  * @param id   ID of this versioned doc element in the database
	  * @param data Wrapped versioned doc element data
	  * @return versioned doc element with the specified id and wrapped data
	  */
	def apply(id: Int, data: VersionedDocElementData): VersionedDocElement = _VersionedDocElement(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the versioned doc element trait
	  * @param id   ID of this versioned doc element in the database
	  * @param data Wrapped versioned doc element data
	  * @author Mikko Hilpinen
	  * @since 28.10.2026
	  */
	private case class _VersionedDocElement(id: Int, 
		data: VersionedDocElementData) extends VersionedDocElement
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: VersionedDocElementData) = copy(data = data)
	}
}

/**
  * Represents a versioned doc element that has already been stored in the database. 
  * Common trait for documentation elements which have current and historical versions.
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElement 
	extends StoredVersionedDocElementLike[VersionedDocElementData, VersionedDocElement] 
		with VersionedDocElementData
{
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

