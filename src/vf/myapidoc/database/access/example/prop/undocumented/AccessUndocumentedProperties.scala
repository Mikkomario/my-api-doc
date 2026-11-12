package vf.myapidoc.database.access.example.prop.undocumented

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.example.response.FilterByExampleResponse
import vf.myapidoc.database.reader.example.UndocumentedPropertyDbReader
import vf.myapidoc.model.stored.example.UndocumentedProperty

object AccessUndocumentedProperties 
	extends WrapRowAccess[AccessUndocumentedPropertyRows] 
		with WrapOneToManyAccess[AccessCombinedUndocumentedProperties] 
		with AccessManyRoot[AccessUndocumentedPropertyRows[UndocumentedProperty]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(UndocumentedPropertyDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessUndocumentedPropertyRows(access)
	override def apply[A](access: TargetingMany[A]) = AccessCombinedUndocumentedProperties(access)
}

/**
  * Used for accessing multiple undocumented properties from the DB at a time
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
abstract class AccessUndocumentedProperties[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingManyLike[A, Repr, AccessUndocumentedProperty[A]] 
		with HasValues[AccessUndocumentedPropertyValues] with FilterUndocumentedProperties[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessUndocumentedPropertyValues(wrapped)
	
	lazy val joinResponses = join(MyApiDocTables.exampleResponse)
	lazy val whereResponses = FilterByExampleResponse(joinResponses)
}

/**
  * Provides access to row-specific undocumented property -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class AccessUndocumentedPropertyRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessUndocumentedProperties[A, AccessUndocumentedPropertyRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessUndocumentedPropertyRows[A], AccessUndocumentedProperty[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessUndocumentedPropertyRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessUndocumentedProperty(target)
}

/**
  * Used for accessing undocumented property items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class AccessCombinedUndocumentedProperties[A](wrapped: TargetingMany[A]) 
	extends AccessUndocumentedProperties[A, AccessCombinedUndocumentedProperties[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedUndocumentedProperties[A], AccessUndocumentedProperty[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedUndocumentedProperties(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessUndocumentedProperty(target)
}

