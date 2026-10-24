package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.NodeAuthTypeLinkDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, NodeAuthTypeLinkDbModel}
import vf.myapidoc.model.stored.api.NodeAuthTypeLink

object AccessNodeAuthTypeLinks 
	extends DeprecatingWrapRowAccess[AccessNodeAuthTypeLinkRows](NodeAuthTypeLinkDbModel) 
		with WrapOneToManyAccess[AccessCombinedNodeAuthTypeLinks] 
		with AccessManyDeprecatingRoot[AccessNodeAuthTypeLinkRows[NodeAuthTypeLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(NodeAuthTypeLinkDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedNodeAuthTypeLinks(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessNodeAuthTypeLinkRows(access)
}

/**
  * Used for accessing multiple node auth type links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessNodeAuthTypeLinks[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessNodeAuthTypeLink[A]] 
		with HasValues[AccessNodeAuthTypeLinkValues] with FilterNodeAuthTypeLinks[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessNodeAuthTypeLinkValues(wrapped)
	
	lazy val joinActiveNodes = join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific node auth type link -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthTypeLinkRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessNodeAuthTypeLinks[A, AccessNodeAuthTypeLinkRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessNodeAuthTypeLinkRows[A], AccessNodeAuthTypeLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessNodeAuthTypeLinkRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessNodeAuthTypeLink(target)
}

/**
  * Used for accessing node auth type link items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedNodeAuthTypeLinks[A](wrapped: TargetingMany[A]) 
	extends AccessNodeAuthTypeLinks[A, AccessCombinedNodeAuthTypeLinks[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedNodeAuthTypeLinks[A], AccessNodeAuthTypeLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedNodeAuthTypeLinks(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessNodeAuthTypeLink(target)
}

