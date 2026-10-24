package vf.myapidoc.database.access.api

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.api.ApiDbReader
import vf.myapidoc.model.stored.api.Api

object AccessApis 
	extends WrapRowAccess[AccessApiRows] with WrapOneToManyAccess[AccessCombinedApis] 
		with AccessManyRoot[AccessApiRows[Api]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(ApiDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessApiRows(access)
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedApis(access)
}

/**
  * Used for accessing multiple apis from the DB at a time
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
abstract class AccessApis[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessApi[A]] with HasValues[AccessApiValues] with FilterApis[Repr] 
		with TargetingManyLike[A, Repr, AccessApi[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiValues(wrapped)
}

/**
  * Provides access to row-specific api -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class AccessApiRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessApis[A, AccessApiRows[A]](wrapped) with AccessRowsWrapper[A, AccessApiRows[A], AccessApi[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessApiRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApi(target)
}

/**
  * Used for accessing api items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class AccessCombinedApis[A](wrapped: TargetingMany[A]) 
	extends AccessApis[A, AccessCombinedApis[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedApis[A], AccessApi[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedApis(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessApi(target)
}

