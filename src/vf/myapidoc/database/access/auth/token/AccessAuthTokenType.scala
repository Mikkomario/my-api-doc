package vf.myapidoc.database.access.auth.token

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.auth.AuthTokenType

object AccessAuthTokenType extends AccessOneDeprecatingRoot[AccessAuthTokenType[AuthTokenType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessAuthTokenTypes.all.head
}

/**
  * Used for accessing individual auth token types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAuthTokenType[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessAuthTokenType[A]] with HasValues[AccessAuthTokenTypeValue] 
		with FilterAuthTokenTypes[AccessAuthTokenType[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAuthTokenTypeValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessAuthTokenType(newTarget)
}

