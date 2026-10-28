package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.ResponseBodySchemaDbReader
import vf.myapidoc.database.storable.api.{ApiNodeDbModel, ApiNodeGroupDbModel, ApiNodeMethodImplementationDbModel, PossibleResponseStatusDbModel, ResponseBodySchemaDbModel}
import vf.myapidoc.model.stored.api.ResponseBodySchema

object AccessResponseBodySchemas 
	extends DeprecatingWrapRowAccess[AccessResponseBodySchemaRows](ResponseBodySchemaDbModel) 
		with WrapOneToManyAccess[AccessCombinedResponseBodySchemas] 
		with AccessManyDeprecatingRoot[AccessResponseBodySchemaRows[ResponseBodySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ResponseBodySchemaDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedResponseBodySchemas(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessResponseBodySchemaRows(access)
}

/**
  * Used for accessing multiple response body schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
abstract class AccessResponseBodySchemas[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessResponseBodySchema[A]]
		with HasValues[AccessResponseBodySchemaValues] with FilterResponseBodySchemas[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessResponseBodySchemaValues(wrapped)
	
	lazy val joinActiveStatuses = join(PossibleResponseStatusDbModel.toActiveJoin)
	lazy val joinActiveMethodImplementations = joinActiveStatuses.join(ApiNodeMethodImplementationDbModel.toActiveJoin)
	lazy val joinActiveNodes = joinActiveMethodImplementations.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific response body schema -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class AccessResponseBodySchemaRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessResponseBodySchemas[A, AccessResponseBodySchemaRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessResponseBodySchemaRows[A], AccessResponseBodySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessResponseBodySchemaRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessResponseBodySchema(target)
}

/**
  * Used for accessing response body schema items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class AccessCombinedResponseBodySchemas[A](wrapped: TargetingMany[A]) 
	extends AccessResponseBodySchemas[A, AccessCombinedResponseBodySchemas[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedResponseBodySchemas[A], AccessResponseBodySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedResponseBodySchemas(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessResponseBodySchema(target)
}

