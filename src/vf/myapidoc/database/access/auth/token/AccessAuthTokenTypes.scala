package vf.myapidoc.database.access.auth.token

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.many.{AccessManyDeprecatingRoot, AccessRowsWrapper, AccessWrapper, DeprecatingWrapRowAccess, TargetingMany, TargetingManyLike, TargetingManyRows, TargetingTimeline, WrapOneToManyAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.auth.AuthTokenTypeDbReader
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.stored.auth.AuthTokenType

object AccessAuthTokenTypes 
	extends DeprecatingWrapRowAccess[AccessAuthTokenTypeRows](AuthTokenTypeDbModel) 
		with WrapOneToManyAccess[AccessCombinedAuthTokenTypes] 
		with AccessManyDeprecatingRoot[AccessAuthTokenTypeRows[AuthTokenType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(AuthTokenTypeDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedAuthTokenTypes(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessAuthTokenTypeRows(access)
}

/**
  * Used for accessing multiple auth token types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessAuthTokenTypes[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessAuthTokenType[A]] with HasValues[AccessAuthTokenTypeValues]
		with FilterAuthTokenTypes[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAuthTokenTypeValues(wrapped)
}

/**
  * Provides access to row-specific auth token type -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAuthTokenTypeRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessAuthTokenTypes[A, AccessAuthTokenTypeRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessAuthTokenTypeRows[A], AccessAuthTokenType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessAuthTokenTypeRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessAuthTokenType(target)
}

/**
  * Used for accessing auth token type items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedAuthTokenTypes[A](wrapped: TargetingMany[A]) 
	extends AccessAuthTokenTypes[A, AccessCombinedAuthTokenTypes[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedAuthTokenTypes[A], AccessAuthTokenType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedAuthTokenTypes(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessAuthTokenType(target)
}

