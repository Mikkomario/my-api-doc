package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.DeprecatableView
import utopia.vault.sql.Condition
import vf.myapidoc.database.props.api.VersionedDocElementDbProps

/**
  * Common trait for access points that may be filtered based on versioned doc element properties
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait FilterVersionedDocElements[+Repr] extends DeprecatableView[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Model that defines versioned doc element database properties
	  */
	def model: VersionedDocElementDbProps
	
	
	// OTHER	--------------------
	
	/**
	  * @param originalUpdateId original update id to target
	  * @return Copy of this access point that only includes versioned doc elements with the specified 
	  * original update id
	  */
	def addedInUpdate(originalUpdateId: Int) = filter(model.originalUpdateId.column <=> originalUpdateId)
	/**
	  * @param originalUpdateIds Targeted original update ids
	  * @return Copy of this access point that only includes versioned doc elements where original update id 
	  * is within the specified value set
	  */
	def addedInUpdates(originalUpdateIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.originalUpdateId, originalUpdateIds))
	
	/**
	  * @param removingUpdateId removing update id to target
	  * @return Copy of this access point that only includes versioned doc elements with the specified 
	  * removing update id
	  */
	def removedInUpdate(removingUpdateId: Int) = filter(model.removingUpdateId.column <=> removingUpdateId)
	/**
	  * @param removingUpdateIds Targeted removing update ids
	  * @return Copy of this access point that only includes versioned doc elements where removing update id 
	  * is within the specified value set
	  */
	def removedInUpdates(removingUpdateIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.removingUpdateId, removingUpdateIds))
}

