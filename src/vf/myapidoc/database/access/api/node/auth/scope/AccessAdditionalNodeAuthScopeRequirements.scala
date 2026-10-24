package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.AdditionalNodeAuthScopeRequirementDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, NodeAuthScopeLinkDbModel}
import vf.myapidoc.model.stored.api.AdditionalNodeAuthScopeRequirement

object AccessAdditionalNodeAuthScopeRequirements 
	extends WrapRowAccess[AccessAdditionalNodeAuthScopeRequirementRows] 
		with WrapOneToManyAccess[AccessCombinedAdditionalNodeAuthScopeRequirements] 
		with AccessManyRoot[AccessAdditionalNodeAuthScopeRequirementRows[AdditionalNodeAuthScopeRequirement]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(AdditionalNodeAuthScopeRequirementDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessAdditionalNodeAuthScopeRequirementRows(access)
	override def apply[A](access: TargetingMany[A]) = AccessCombinedAdditionalNodeAuthScopeRequirements(access)
}

/**
  * Used for accessing multiple additional node auth scope requirements from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessAdditionalNodeAuthScopeRequirements[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessAdditionalNodeAuthScopeRequirement[A]] 
		with HasValues[AccessAdditionalNodeAuthScopeRequirementValues] 
		with FilterAdditionalNodeAuthScopeRequirements[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAdditionalNodeAuthScopeRequirementValues(wrapped)
	
	lazy val joinScopeLinks = join(MyApiDocTables.nodeAuthScopeLink)
	lazy val whereScopeLinks = FilterByNodeAuthScopeLink(joinScopeLinks)
	
	lazy val joinActiveScopeLinks = join(NodeAuthScopeLinkDbModel.toActiveJoin)
	lazy val joinActiveNodes = joinActiveScopeLinks.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific additional node auth scope requirement -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessAdditionalNodeAuthScopeRequirementRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessAdditionalNodeAuthScopeRequirements[A, AccessAdditionalNodeAuthScopeRequirementRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessAdditionalNodeAuthScopeRequirementRows[A], AccessAdditionalNodeAuthScopeRequirement[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = 
		AccessAdditionalNodeAuthScopeRequirementRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessAdditionalNodeAuthScopeRequirement(target)
}

/**
  * Used for accessing additional node auth scope requirement items that have been combined with 
  * one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedAdditionalNodeAuthScopeRequirements[A](wrapped: TargetingMany[A]) 
	extends AccessAdditionalNodeAuthScopeRequirements[A, AccessCombinedAdditionalNodeAuthScopeRequirements[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedAdditionalNodeAuthScopeRequirements[A], AccessAdditionalNodeAuthScopeRequirement[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = 
		AccessCombinedAdditionalNodeAuthScopeRequirements(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessAdditionalNodeAuthScopeRequirement(target)
}

