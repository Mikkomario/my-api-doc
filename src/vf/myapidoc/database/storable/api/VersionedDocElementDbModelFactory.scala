package vf.myapidoc.database.storable.api

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import vf.myapidoc.database.props.api.{VersionedDocElementDbProps, VersionedDocElementDbPropsWrapper}
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.stored.api.VersionedDocElement

object VersionedDocElementDbModelFactory
{
	// OTHER	--------------------
	
	/**
	  * @return A factory for constructing versioned doc element database models
	  */
	def apply(table: Table, dbProps: VersionedDocElementDbProps) = 
		VersionedDocElementDbModelFactoryImpl(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * Used for constructing VersionedDocElementDbModel instances and for inserting versioned doc 
	  * elements to the database
	  * @param table                      Table targeted by these models
	  * @param versionedDocElementDbProps Properties that specify how the database interactions are 
	  *                                   performed
	  * @author Mikko Hilpinen
	  * @since 28.10.2026, v0.1
	  */
	case class VersionedDocElementDbModelFactoryImpl(table: Table, 
		versionedDocElementDbProps: VersionedDocElementDbProps) 
		extends VersionedDocElementDbModelFactory with VersionedDocElementDbPropsWrapper
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		
		// IMPLEMENTED	--------------------
		
		override def apply(data: VersionedDocElementData): VersionedDocElementDbModel = 
			apply(None, Some(data.originalUpdateId), data.removingUpdateId)
		
		override def withId(id: Int) = apply(id = Some(id))
		
		override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
			Some(originalUpdateId))
		
		override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
			Some(removingUpdateId))
		
		override protected def complete(id: Value, data: VersionedDocElementData) = 
			VersionedDocElement(id.getInt, data)
		
		
		// OTHER	--------------------
		
		/**
		  * @param id versioned doc element database id
		  * @return Constructs a new versioned doc element database model with the specified properties
		  */
		def apply(id: Option[Int] = None, originalUpdateId: Option[Int] = None, 
			removingUpdateId: Option[Int] = None): VersionedDocElementDbModel = 
			_VersionedDocElementDbModel(table, versionedDocElementDbProps, id, originalUpdateId, 
				removingUpdateId)
	}
	
	/**
	  * Used for interacting with VersionedDocElements in the database
	  * @param table   Table interacted with when using this model
	  * @param dbProps Configurations of the interacted database properties
	  * @param id      versioned doc element database id
	  * @author Mikko Hilpinen
	  * @since 28.10.2026, v0.1
	  */
	private case class _VersionedDocElementDbModel(table: Table, dbProps: VersionedDocElementDbProps, 
		id: Option[Int] = None, originalUpdateId: Option[Int] = None, removingUpdateId: Option[Int] = None) 
		extends VersionedDocElementDbModel
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param id               ID to assign to the new model (default = currently assigned id)
		  * @param originalUpdateId original update id to assign to the new model (default = currently 
		  *                         assigned value)
		  * @param removingUpdateId removing update id to assign to the new model (default = currently 
		  *                         assigned value)
		  * @return Copy of this model with the specified versioned doc element properties
		  */
		override protected def copyVersionedDocElement(id: Option[Int] = id, 
			originalUpdateId: Option[Int] = originalUpdateId, 
			removingUpdateId: Option[Int] = removingUpdateId) = 
			copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	}
}

/**
  * Common trait for factories yielding versioned doc element database models
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbModelFactory 
	extends VersionedDocElementDbModelFactoryLike[VersionedDocElementDbModel, VersionedDocElement, 
		VersionedDocElementData]

