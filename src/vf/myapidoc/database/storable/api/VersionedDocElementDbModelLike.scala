package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.Storable
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.factory.api.VersionedDocElementFactory

/**
  * Common trait for database models used for interacting with versioned doc element data in the 
  * database
  * @tparam Repr Type of this DB model
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbModelLike[+Repr] 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, Repr] 
		with VersionedDocElementFactory[Repr]
{
	// ABSTRACT	--------------------
	
	def originalUpdateId: Option[Int]
	
	def removingUpdateId: Option[Int]
	
	/**
	  * Access to the database properties that are utilized in this model
	  */
	def dbProps: VersionedDocElementDbProps
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @return Copy of this model with the specified versioned doc element properties
	  */
	protected def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, 
		removingUpdateId: Option[Int] = removingUpdateId): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def valueProperties: Seq[(String, Value)] = 
		Vector(dbProps.id.name -> id, dbProps.originalUpdateId.name -> originalUpdateId, 
			dbProps.removingUpdateId.name -> removingUpdateId)
	
	override def withId(id: Int) = copyVersionedDocElement(id = Some(id))
	
	override def withOriginalUpdateId(originalUpdateId: Int) = 
		copyVersionedDocElement(originalUpdateId = Some(originalUpdateId))
	
	override def withRemovingUpdateId(removingUpdateId: Int) = 
		copyVersionedDocElement(removingUpdateId = Some(removingUpdateId))
}

