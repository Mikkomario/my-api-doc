package vf.myapidoc.database.access.schema.prop.types

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.schema.model.FilterByModelSchema
import vf.myapidoc.database.access.schema.prop.FilterByPropertySchema
import vf.myapidoc.database.reader.schema.AlternativePropertyTypeDbReader
import vf.myapidoc.database.storable.schema.{AlternativePropertyTypeDbModel, ModelSchemaDbModel, PropertySchemaDbModel}
import vf.myapidoc.model.stored.schema.AlternativePropertyType

object AccessAlternativePropertyTypes 
	extends DeprecatingWrapRowAccess[AccessAlternativePropertyTypeRows](AlternativePropertyTypeDbModel) 
		with WrapOneToManyAccess[AccessCombinedAlternativePropertyTypes] 
		with AccessManyDeprecatingRoot[AccessAlternativePropertyTypeRows[AlternativePropertyType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(AlternativePropertyTypeDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedAlternativePropertyTypes(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessAlternativePropertyTypeRows(access)
}

/**
  * Used for accessing multiple alternative property types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessAlternativePropertyTypes[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessAlternativePropertyType[A]] 
		with HasValues[AccessAlternativePropertyTypeValues] with FilterAlternativePropertyTypes[Repr] 
		with TargetingManyLike[A, Repr, AccessAlternativePropertyType[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAlternativePropertyTypeValues(wrapped)
	
	lazy val joinActiveProperties = {
		val propModel = PropertySchemaDbModel
		join(propModel.table.onlyJoinIf(propModel.activeCondition))
	}
	lazy val whereActiveProperties = FilterByPropertySchema(joinActiveProperties)
	
	lazy val joinActiveModels = {
		val model = ModelSchemaDbModel
		joinActiveProperties.join(model.table.onlyJoinIf(model.activeCondition))
	}
	lazy val whereActiveModels = FilterByModelSchema(joinActiveModels)
}

/**
  * Provides access to row-specific alternative property type -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAlternativePropertyTypeRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessAlternativePropertyTypes[A, AccessAlternativePropertyTypeRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessAlternativePropertyTypeRows[A], AccessAlternativePropertyType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessAlternativePropertyTypeRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessAlternativePropertyType(target)
}

/**
  * Used for accessing alternative property type items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedAlternativePropertyTypes[A](wrapped: TargetingMany[A]) 
	extends AccessAlternativePropertyTypes[A, AccessCombinedAlternativePropertyTypes[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedAlternativePropertyTypes[A], AccessAlternativePropertyType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedAlternativePropertyTypes(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessAlternativePropertyType(target)
}

