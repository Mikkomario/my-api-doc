package vf.myapidoc.database.access.api.params.path

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.node.FilterByApiNode
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.PathParametersSchemaLinkDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, PathParametersSchemaLinkDbModel}
import vf.myapidoc.model.stored.api.PathParametersSchemaLink

object AccessPathParameterSchemaLinks 
	extends DeprecatingWrapRowAccess[AccessPathParametersSchemaLinkRows](PathParametersSchemaLinkDbModel) 
		with WrapOneToManyAccess[AccessCombinedPathParameterSchemaLinks] 
		with AccessManyDeprecatingRoot[AccessPathParametersSchemaLinkRows[PathParametersSchemaLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(PathParametersSchemaLinkDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedPathParameterSchemaLinks(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessPathParametersSchemaLinkRows(access)
}

/**
  * Used for accessing multiple path parameter schema links from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessPathParameterSchemaLinks[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessPathParametersSchemaLink[A]] 
		with HasValues[AccessPathParametersSchemaLinkValues] with FilterPathParameterSchemaLinks[Repr] 
		with TargetingManyLike[A, Repr, AccessPathParametersSchemaLink[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPathParametersSchemaLinkValues(wrapped)
	
	lazy val joinNodes = join(MyApiDocTables.apiNode)
	lazy val whereNodes = FilterByApiNode(joinNodes)
	
	lazy val joinActiveNodes = {
		val node = ApiNodeDbModel
		join(node.table.onlyJoinIf(node.activeCondition))
	}
	
	lazy val joinActiveNodeGroups = {
		val group = ApiNodeGroupDbModel
		joinActiveNodes.join(group.table.onlyJoinIf(group.activeCondition))
	}
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific path parameters schema link -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPathParametersSchemaLinkRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessPathParameterSchemaLinks[A, AccessPathParametersSchemaLinkRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessPathParametersSchemaLinkRows[A], AccessPathParametersSchemaLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessPathParametersSchemaLinkRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessPathParametersSchemaLink(target)
}

/**
  * Used for accessing path parameters schema link items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedPathParameterSchemaLinks[A](wrapped: TargetingMany[A]) 
	extends AccessPathParameterSchemaLinks[A, AccessCombinedPathParameterSchemaLinks[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedPathParameterSchemaLinks[A], AccessPathParametersSchemaLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedPathParameterSchemaLinks(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessPathParametersSchemaLink(target)
}

