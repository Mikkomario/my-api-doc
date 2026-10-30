package vf.myapidoc.database.access.example.prop

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.example.response.FilterByExampleResponse
import vf.myapidoc.database.reader.example.ExamplePropertyValueDbReader
import vf.myapidoc.model.stored.example.ExamplePropertyValue

object AccessExamplePropertyValues 
	extends WrapRowAccess[AccessExamplePropertyValueRows] 
		with WrapOneToManyAccess[AccessCombinedExamplePropertyValues] 
		with AccessManyRoot[AccessExamplePropertyValueRows[ExamplePropertyValue]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(ExamplePropertyValueDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessExamplePropertyValueRows(access)
	override def apply[A](access: TargetingMany[A]) = AccessCombinedExamplePropertyValues(access)
}

/**
  * Used for accessing multiple example property values from the DB at a time
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
abstract class AccessExamplePropertyValues[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessExamplePropertyValue[A]] 
		with HasValues[AccessExamplePropertyValueValueValues] with FilterExamplePropertyValues[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessExamplePropertyValueValueValues(wrapped)
	
	lazy val joinResponses = join(MyApiDocTables.exampleResponse)
	lazy val whereResponses = FilterByExampleResponse(joinResponses)
}

/**
  * Provides access to row-specific example property value -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExamplePropertyValueRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessExamplePropertyValues[A, AccessExamplePropertyValueRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessExamplePropertyValueRows[A], AccessExamplePropertyValue[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessExamplePropertyValueRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessExamplePropertyValue(target)
}

/**
  * Used for accessing example property value items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessCombinedExamplePropertyValues[A](wrapped: TargetingMany[A]) 
	extends AccessExamplePropertyValues[A, AccessCombinedExamplePropertyValues[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedExamplePropertyValues[A], AccessExamplePropertyValue[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedExamplePropertyValues(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessExamplePropertyValue(target)
}

