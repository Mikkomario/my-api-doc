package vf.myapidoc.database.access.schema.prop

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.schema.model.FilterByModelSchema
import vf.myapidoc.database.reader.schema.PropertySchemaDbReader
import vf.myapidoc.database.storable.schema.{ModelSchemaDbModel, PropertySchemaDbModel}
import vf.myapidoc.model.stored.schema.StoredPropertySchema

object AccessPropertySchemas 
	extends DeprecatingWrapRowAccess[AccessPropertySchemaRows](PropertySchemaDbModel) 
		with WrapOneToManyAccess[AccessCombinedPropertySchemas] 
		with AccessManyDeprecatingRoot[AccessPropertySchemaRows[StoredPropertySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(PropertySchemaDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedPropertySchemas(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessPropertySchemaRows(access)
}

/**
  * Used for accessing multiple property schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessPropertySchemas[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessPropertySchema[A]] with HasValues[AccessPropertySchemaValues]
		with FilterPropertySchemas[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPropertySchemaValues(wrapped)
	
	lazy val joinActiveModels = {
		val modelModel = ModelSchemaDbModel
		join(modelModel.table.onlyJoinIf(modelModel.activeCondition))
	}
	
	lazy val whereActiveModels = FilterByModelSchema(joinActiveModels)
}

/**
  * Provides access to row-specific property schema -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPropertySchemaRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessPropertySchemas[A, AccessPropertySchemaRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessPropertySchemaRows[A], AccessPropertySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessPropertySchemaRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessPropertySchema(target)
}

/**
  * Used for accessing property schema items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedPropertySchemas[A](wrapped: TargetingMany[A]) 
	extends AccessPropertySchemas[A, AccessCombinedPropertySchemas[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedPropertySchemas[A], AccessPropertySchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedPropertySchemas(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessPropertySchema(target)
}

