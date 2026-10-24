package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.NodeAuthScopeLinkDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, NodeAuthScopeLinkDbModel}
import vf.myapidoc.model.stored.api.NodeAuthScopeLink

object AccessNodeAuthScopeLinks 
	extends DeprecatingWrapRowAccess[AccessNodeAuthScopeLinkRows](NodeAuthScopeLinkDbModel) 
		with WrapOneToManyAccess[AccessCombinedNodeAuthScopeLinks] 
		with AccessManyDeprecatingRoot[AccessNodeAuthScopeLinkRows[NodeAuthScopeLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(NodeAuthScopeLinkDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedNodeAuthScopeLinks(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessNodeAuthScopeLinkRows(access)
}

/**
  * Used for accessing multiple node auth scope links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessNodeAuthScopeLinks[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessNodeAuthScopeLink[A]] 
		with HasValues[AccessNodeAuthScopeLinkValues] with FilterNodeAuthScopeLinks[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessNodeAuthScopeLinkValues(wrapped)
	
	lazy val joinActiveNodes = join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific node auth scope link -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthScopeLinkRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessNodeAuthScopeLinks[A, AccessNodeAuthScopeLinkRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessNodeAuthScopeLinkRows[A], AccessNodeAuthScopeLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessNodeAuthScopeLinkRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessNodeAuthScopeLink(target)
}

/**
  * Used for accessing node auth scope link items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedNodeAuthScopeLinks[A](wrapped: TargetingMany[A]) 
	extends AccessNodeAuthScopeLinks[A, AccessCombinedNodeAuthScopeLinks[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedNodeAuthScopeLinks[A], AccessNodeAuthScopeLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedNodeAuthScopeLinks(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessNodeAuthScopeLink(target)
}

