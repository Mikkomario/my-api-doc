package vf.myapidoc.database.access.schema.prop.value

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.schema.model.FilterByModelSchema
import vf.myapidoc.database.reader.schema.PossiblePropertyValueDbReader
import vf.myapidoc.database.storable.schema.{ModelSchemaDbModel, PossiblePropertyValueDbModel, PropertySchemaDbModel}
import vf.myapidoc.model.stored.schema.PossiblePropertyValue

object AccessPossiblePropertyValues 
	extends DeprecatingWrapRowAccess[AccessPossiblePropertyValueRows](PossiblePropertyValueDbModel) 
		with WrapOneToManyAccess[AccessCombinedPossiblePropertyValues] 
		with AccessManyDeprecatingRoot[AccessPossiblePropertyValueRows[PossiblePropertyValue]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(PossiblePropertyValueDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedPossiblePropertyValues(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessPossiblePropertyValueRows(access)
}

/**
  * Used for accessing multiple possible property values from the DB at a time
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
abstract class AccessPossiblePropertyValues[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessPossiblePropertyValue[A]]
		with HasValues[AccessPossiblePropertyValueValueValues] with FilterPossiblePropertyValues[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPossiblePropertyValueValueValues(wrapped)
	
	lazy val joinActiveProperties = join(PropertySchemaDbModel.toActiveJoin)
	
	lazy val joinActiveModels = joinActiveProperties.join(ModelSchemaDbModel.toActiveJoin)
	lazy val whereActiveModels = FilterByModelSchema(joinActiveModels)
}

/**
  * Provides access to row-specific possible property value -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class AccessPossiblePropertyValueRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessPossiblePropertyValues[A, AccessPossiblePropertyValueRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessPossiblePropertyValueRows[A], AccessPossiblePropertyValue[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessPossiblePropertyValueRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessPossiblePropertyValue(target)
}

/**
  * Used for accessing possible property value items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class AccessCombinedPossiblePropertyValues[A](wrapped: TargetingMany[A]) 
	extends AccessPossiblePropertyValues[A, AccessCombinedPossiblePropertyValues[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedPossiblePropertyValues[A], AccessPossiblePropertyValue[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedPossiblePropertyValues(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessPossiblePropertyValue(target)
}

