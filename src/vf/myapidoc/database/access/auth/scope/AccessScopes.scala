package vf.myapidoc.database.access.auth.scope

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.auth.ScopeDbReader
import vf.myapidoc.database.storable.auth.ScopeDbModel
import vf.myapidoc.model.stored.auth.Scope

object AccessScopes 
	extends DeprecatingWrapRowAccess[AccessScopeRows](ScopeDbModel) 
		with WrapOneToManyAccess[AccessCombinedScopes] with AccessManyDeprecatingRoot[AccessScopeRows[Scope]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ScopeDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedScopes(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessScopeRows(access)
}

/**
  * Used for accessing multiple scopes from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessScopes[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessScope[A]] with HasValues[AccessScopeValues] 
		with FilterScopes[Repr] with TargetingManyLike[A, Repr, AccessScope[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessScopeValues(wrapped)
}

/**
  * Provides access to row-specific scope -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScopeRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessScopes[A, AccessScopeRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessScopeRows[A], AccessScope[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessScopeRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessScope(target)
}

/**
  * Used for accessing scope items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedScopes[A](wrapped: TargetingMany[A]) 
	extends AccessScopes[A, AccessCombinedScopes[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedScopes[A], AccessScope[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedScopes(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessScope(target)
}

