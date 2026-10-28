package vf.myapidoc.database.access.api.params.query

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.access.api.node.impl.FilterByApiNodeMethodImplementation
import vf.myapidoc.database.reader.api.QueryParametersSchemaLinkDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodeMethodImplementationDbModel, QueryParametersSchemaLinkDbModel}
import vf.myapidoc.model.stored.api.QueryParametersSchemaLink

object AccessQueryParameterSchemaLinks 
	extends DeprecatingWrapRowAccess[AccessQueryParametersSchemaLinkRows](QueryParametersSchemaLinkDbModel) 
		with WrapOneToManyAccess[AccessCombinedQueryParameterSchemaLinks] 
		with AccessManyDeprecatingRoot[AccessQueryParametersSchemaLinkRows[QueryParametersSchemaLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(QueryParametersSchemaLinkDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedQueryParameterSchemaLinks(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessQueryParametersSchemaLinkRows(access)
}

/**
  * Used for accessing multiple query parameter schema links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessQueryParameterSchemaLinks[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessQueryParametersSchemaLink[A]]
		with HasValues[AccessQueryParametersSchemaLinkValues] with FilterQueryParameterSchemaLinks[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessQueryParametersSchemaLinkValues(wrapped)
	
	lazy val joinMethodImplementations = join(MyApiDocTables.apiNodeMethodImplementation)
	lazy val whereMethodImplementations = FilterByApiNodeMethodImplementation(joinMethodImplementations)
	
	lazy val joinActiveMethodImplementations = join(ApiNodeMethodImplementationDbModel.toActiveJoin)
	lazy val joinActiveNodes = joinActiveMethodImplementations.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific query parameters schema link -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessQueryParametersSchemaLinkRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessQueryParameterSchemaLinks[A, AccessQueryParametersSchemaLinkRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessQueryParametersSchemaLinkRows[A], AccessQueryParametersSchemaLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = 
		AccessQueryParametersSchemaLinkRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessQueryParametersSchemaLink(target)
}

/**
  * Used for accessing query parameters schema link items that have been combined with 
  * one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedQueryParameterSchemaLinks[A](wrapped: TargetingMany[A]) 
	extends AccessQueryParameterSchemaLinks[A, AccessCombinedQueryParameterSchemaLinks[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedQueryParameterSchemaLinks[A], AccessQueryParametersSchemaLink[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = 
		AccessCombinedQueryParameterSchemaLinks(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessQueryParametersSchemaLink(target)
}

