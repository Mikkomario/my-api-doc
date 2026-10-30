package vf.myapidoc.database.access.example.response

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.example.ExampleResponseDbReader
import vf.myapidoc.model.stored.example.ExampleResponse

object AccessExampleResponses 
	extends WrapRowAccess[AccessExampleResponseRows] with WrapOneToManyAccess[AccessCombinedExampleResponses] 
		with AccessManyRoot[AccessExampleResponseRows[ExampleResponse]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(ExampleResponseDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessExampleResponseRows(access)
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedExampleResponses(access)
}

/**
  * Used for accessing multiple example responses from the DB at a time
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
abstract class AccessExampleResponses[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessExampleResponse[A]] with HasValues[AccessExampleResponseValues] 
		with FilterExampleResponses[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessExampleResponseValues(wrapped)
}

/**
  * Provides access to row-specific example response -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExampleResponseRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessExampleResponses[A, AccessExampleResponseRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessExampleResponseRows[A], AccessExampleResponse[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessExampleResponseRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessExampleResponse(target)
}

/**
  * Used for accessing example response items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessCombinedExampleResponses[A](wrapped: TargetingMany[A]) 
	extends AccessExampleResponses[A, AccessCombinedExampleResponses[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedExampleResponses[A], AccessExampleResponse[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedExampleResponses(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessExampleResponse(target)
}

