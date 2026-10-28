package vf.myapidoc.database.access.api.update

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.api.DocUpdateDbReader
import vf.myapidoc.model.stored.api.DocUpdate

object AccessDocUpdates 
	extends WrapRowAccess[AccessDocUpdateRows] with WrapOneToManyAccess[AccessCombinedDocUpdates] 
		with AccessManyRoot[AccessDocUpdateRows[DocUpdate]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(DocUpdateDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessDocUpdateRows(access)
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedDocUpdates(access)
}

/**
  * Used for accessing multiple doc updates from the DB at a time
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
abstract class AccessDocUpdates[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessDocUpdate[A]] with HasValues[AccessDocUpdateValues] 
		with FilterDocUpdates[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessDocUpdateValues(wrapped)
}

/**
  * Provides access to row-specific doc update -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class AccessDocUpdateRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessDocUpdates[A, AccessDocUpdateRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessDocUpdateRows[A], AccessDocUpdate[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessDocUpdateRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessDocUpdate(target)
}

/**
  * Used for accessing doc update items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class AccessCombinedDocUpdates[A](wrapped: TargetingMany[A]) 
	extends AccessDocUpdates[A, AccessCombinedDocUpdates[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedDocUpdates[A], AccessDocUpdate[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedDocUpdates(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessDocUpdate(target)
}

