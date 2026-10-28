package vf.myapidoc.database.access.api.body

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.RequestBodySchemaDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodeMethodImplementationDbModel, RequestBodySchemaDbModel}
import vf.myapidoc.model.stored.api.RequestBodySchema

object AccessRequestBodySchemas 
	extends DeprecatingWrapRowAccess[AccessRequestBodySchemaRows](RequestBodySchemaDbModel) 
		with WrapOneToManyAccess[AccessCombinedRequestBodySchemas] 
		with AccessManyDeprecatingRoot[AccessRequestBodySchemaRows[RequestBodySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(RequestBodySchemaDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedRequestBodySchemas(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessRequestBodySchemaRows(access)
}

/**
  * Used for accessing multiple request body schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
abstract class AccessRequestBodySchemas[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessRequestBodySchema[A]]
		with HasValues[AccessRequestBodySchemaValues] with FilterRequestBodySchemas[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessRequestBodySchemaValues(wrapped)
	
	lazy val joinActiveMethodImplementations = join(ApiNodeMethodImplementationDbModel.toActiveJoin)
	
	lazy val joinActiveNodes = joinActiveMethodImplementations.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific request body schema -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessRequestBodySchemaRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessRequestBodySchemas[A, AccessRequestBodySchemaRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessRequestBodySchemaRows[A], AccessRequestBodySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessRequestBodySchemaRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessRequestBodySchema(target)
}

/**
  * Used for accessing request body schema items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessCombinedRequestBodySchemas[A](wrapped: TargetingMany[A]) 
	extends AccessRequestBodySchemas[A, AccessCombinedRequestBodySchemas[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedRequestBodySchemas[A], AccessRequestBodySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedRequestBodySchemas(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessRequestBodySchema(target)
}

