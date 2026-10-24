package vf.myapidoc.database.access.api.node.impl

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.ApiNodeMethodImplementationDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodeMethodImplementationDbModel}
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementation

object AccessApiNodeMethodImplementations 
	extends DeprecatingWrapRowAccess[AccessApiNodeMethodImplementationRows](ApiNodeMethodImplementationDbModel) 
		with WrapOneToManyAccess[AccessCombinedApiNodeMethodImplementations] 
		with AccessManyDeprecatingRoot[AccessApiNodeMethodImplementationRows[ApiNodeMethodImplementation]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ApiNodeMethodImplementationDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedApiNodeMethodImplementations(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = 
		AccessApiNodeMethodImplementationRows(access)
}

/**
  * Used for accessing multiple api node method implementations from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessApiNodeMethodImplementations[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessApiNodeMethodImplementation[A]] 
		with HasValues[AccessApiNodeMethodImplementationValues] with FilterApiNodeMethodImplementations[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeMethodImplementationValues(wrapped)
	
	lazy val joinActiveNodes = join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific api node method implementation -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementationRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessApiNodeMethodImplementations[A, AccessApiNodeMethodImplementationRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessApiNodeMethodImplementationRows[A], AccessApiNodeMethodImplementation[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = 
		AccessApiNodeMethodImplementationRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessApiNodeMethodImplementation(target)
}

/**
  * Used for accessing api node method implementation items that have been combined with 
  * one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedApiNodeMethodImplementations[A](wrapped: TargetingMany[A]) 
	extends AccessApiNodeMethodImplementations[A, AccessCombinedApiNodeMethodImplementations[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedApiNodeMethodImplementations[A], AccessApiNodeMethodImplementation[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = 
		AccessCombinedApiNodeMethodImplementations(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessApiNodeMethodImplementation(target)
}

