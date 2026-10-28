package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType

object VersionedDocElementData extends FromModelFactoryWithSchema[VersionedDocElementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")), 
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		VersionedDocElementData(valid("originalUpdateId").getInt, valid("removingUpdateId").int)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new versioned doc element
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @return versioned doc element with the specified properties
	  */
	def apply(originalUpdateId: Int, removingUpdateId: Option[Int] = None): VersionedDocElementData = 
		_VersionedDocElementData(originalUpdateId, removingUpdateId)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the versioned doc element data trait
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @author Mikko Hilpinen
	  * @since 28.10.2026
	  */
	private case class _VersionedDocElementData(originalUpdateId: Int, removingUpdateId: Option[Int] = None) 
		extends VersionedDocElementData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param originalUpdateId ID of the update that introduced this element (version)
		  * @param removingUpdateId ID of the update that removed or replaced this element (version)
		  */
		override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int] = None) = 
			_VersionedDocElementData(originalUpdateId, removingUpdateId)
	}
}

/**
  * Common trait for documentation elements which have current and historical versions.
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementData extends VersionedDocElementDataLike[VersionedDocElementData]

