package vf.myapidoc.database.access.api.node.group

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.api.{ApiNodeGroupDbReader, CompleteApiNodeGroupDbReader}
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel
import vf.myapidoc.model.stored.api.ApiNodeGroup

object AccessApiNodeGroups 
	extends DeprecatingWrapRowAccess[AccessApiNodeGroupRows](ApiNodeGroupDbModel) 
		with WrapOneToManyAccess[AccessCombinedApiNodeGroups] 
		with AccessManyDeprecatingRoot[AccessApiNodeGroupRows[ApiNodeGroup]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ApiNodeGroupDbReader).all
	
	/**
	 * Access to API node groups including the grouped nodes
	 */
	lazy val complete = apply(CompleteApiNodeGroupDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedApiNodeGroups(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessApiNodeGroupRows(access)
}

/**
  * Used for accessing multiple api node groups from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessApiNodeGroups[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessApiNodeGroup[A]] with HasValues[AccessApiNodeGroupValues]
		with FilterApiNodeGroups[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeGroupValues(wrapped)
}

/**
  * Provides access to row-specific api node group -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroupRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessApiNodeGroups[A, AccessApiNodeGroupRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessApiNodeGroupRows[A], AccessApiNodeGroup[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessApiNodeGroupRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNodeGroup(target)
}

/**
  * Used for accessing api node group items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedApiNodeGroups[A](wrapped: TargetingMany[A]) 
	extends AccessApiNodeGroups[A, AccessCombinedApiNodeGroups[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedApiNodeGroups[A], AccessApiNodeGroup[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedApiNodeGroups(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNodeGroup(target)
}

