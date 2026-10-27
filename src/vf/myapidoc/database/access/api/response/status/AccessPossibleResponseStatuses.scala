package vf.myapidoc.database.access.api.response.status

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.PossibleResponseStatusDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodeMethodImplementationDbModel, PossibleResponseStatusDbModel}
import vf.myapidoc.model.stored.api.PossibleResponseStatus

object AccessPossibleResponseStatuses 
	extends DeprecatingWrapRowAccess[AccessPossibleResponseStatusRows](PossibleResponseStatusDbModel) 
		with WrapOneToManyAccess[AccessCombinedPossibleResponseStatuses] 
		with AccessManyDeprecatingRoot[AccessPossibleResponseStatusRows[PossibleResponseStatus]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(PossibleResponseStatusDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedPossibleResponseStatuses(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessPossibleResponseStatusRows(access)
}

/**
  * Used for accessing multiple possible response statuses from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessPossibleResponseStatuses[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessPossibleResponseStatus[A]] 
		with HasValues[AccessPossibleResponseStatusValues] with FilterPossibleResponseStatuses[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPossibleResponseStatusValues(wrapped)
	
	lazy val joinActiveMethodImplementations = join(ApiNodeMethodImplementationDbModel.toActiveJoin)
	lazy val joinActiveNodes = joinActiveMethodImplementations.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific possible response status -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessPossibleResponseStatusRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessPossibleResponseStatuses[A, AccessPossibleResponseStatusRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessPossibleResponseStatusRows[A], AccessPossibleResponseStatus[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessPossibleResponseStatusRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessPossibleResponseStatus(target)
}

/**
  * Used for accessing possible response status items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedPossibleResponseStatuses[A](wrapped: TargetingMany[A]) 
	extends AccessPossibleResponseStatuses[A, AccessCombinedPossibleResponseStatuses[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedPossibleResponseStatuses[A], AccessPossibleResponseStatus[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = 
		AccessCombinedPossibleResponseStatuses(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessPossibleResponseStatus(target)
}

