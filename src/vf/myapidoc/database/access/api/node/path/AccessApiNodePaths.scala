package vf.myapidoc.database.access.api.node.path

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.ApiNodePathDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodePathDbModel}
import vf.myapidoc.model.stored.api.ApiNodePath

object AccessApiNodePaths 
	extends DeprecatingWrapRowAccess[AccessApiNodePathRows](ApiNodePathDbModel)
		with WrapOneToManyAccess[AccessCombinedApiNodePaths]
		with AccessManyDeprecatingRoot[AccessApiNodePathRows[ApiNodePath]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ApiNodePathDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrap[I](access: TargetingManyRows[I]): AccessApiNodePathRows[I] =
		AccessApiNodePathRows(access)
	override def apply[A](access: TargetingMany[A]) = AccessCombinedApiNodePaths(access)
}

/**
  * Used for accessing multiple api node paths from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
abstract class AccessApiNodePaths[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessApiNodePath[A]] with HasValues[AccessApiNodePathValues] 
		with FilterApiNodePaths[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodePathValues(wrapped)
	
	lazy val joinActiveNodes = join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific api node path -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class AccessApiNodePathRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessApiNodePaths[A, AccessApiNodePathRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessApiNodePathRows[A], AccessApiNodePath[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessApiNodePathRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNodePath(target)
}

/**
  * Used for accessing api node path items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class AccessCombinedApiNodePaths[A](wrapped: TargetingMany[A]) 
	extends AccessApiNodePaths[A, AccessCombinedApiNodePaths[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedApiNodePaths[A], AccessApiNodePath[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedApiNodePaths(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNodePath(target)
}

