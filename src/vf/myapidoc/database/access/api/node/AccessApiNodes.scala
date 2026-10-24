package vf.myapidoc.database.access.api.node

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.ApiNodeDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel}
import vf.myapidoc.model.stored.api.ApiNode

object AccessApiNodes 
	extends DeprecatingWrapRowAccess[AccessApiNodeRows](ApiNodeDbModel) 
		with WrapOneToManyAccess[AccessCombinedApiNodes] 
		with AccessManyDeprecatingRoot[AccessApiNodeRows[ApiNode]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ApiNodeDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedApiNodes(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessApiNodeRows(access)
}

/**
  * Used for accessing multiple api nodes from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessApiNodes[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessApiNode[A]] with HasValues[AccessApiNodeValues] 
		with FilterApiNodes[Repr] with TargetingManyLike[A, Repr, AccessApiNode[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeValues(wrapped)
	
	lazy val joinActiveGroups = {
		val groupModel = ApiNodeGroupDbModel
		join(groupModel.table.onlyJoinIf(groupModel.activeCondition))
	}
	lazy val whereActiveGroups = FilterByApiNodeGroup(joinActiveGroups)
}

/**
  * Provides access to row-specific api node -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessApiNodes[A, AccessApiNodeRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessApiNodeRows[A], AccessApiNode[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessApiNodeRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNode(target)
}

/**
  * Used for accessing api node items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedApiNodes[A](wrapped: TargetingMany[A]) 
	extends AccessApiNodes[A, AccessCombinedApiNodes[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedApiNodes[A], AccessApiNode[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedApiNodes(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApiNode(target)
}

