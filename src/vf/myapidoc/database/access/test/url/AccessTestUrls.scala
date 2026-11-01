package vf.myapidoc.database.access.test.url

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.test.TestUrlDbReader
import vf.myapidoc.model.stored.test.TestUrl

object AccessTestUrls 
	extends WrapRowAccess[AccessTestUrlRows] with WrapOneToManyAccess[AccessCombinedTestUrls] 
		with AccessManyRoot[AccessTestUrlRows[TestUrl]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(TestUrlDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessTestUrlRows(access)
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedTestUrls(access)
}

/**
  * Used for accessing multiple test urls from the DB at a time
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
abstract class AccessTestUrls[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessTestUrl[A]] with HasValues[AccessTestUrlValues] 
		with FilterTestUrls[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessTestUrlValues(wrapped)
}

/**
  * Provides access to row-specific test url -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestUrlRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessTestUrls[A, AccessTestUrlRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessTestUrlRows[A], AccessTestUrl[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessTestUrlRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessTestUrl(target)
}

/**
  * Used for accessing test url items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessCombinedTestUrls[A](wrapped: TargetingMany[A]) 
	extends AccessTestUrls[A, AccessCombinedTestUrls[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedTestUrls[A], AccessTestUrl[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedTestUrls(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessTestUrl(target)
}

