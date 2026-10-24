package vf.myapidoc.database.access.auth.scope

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.auth.Scope

object AccessScope extends AccessOneDeprecatingRoot[AccessScope[Scope]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessScopes.all.head
}

/**
  * Used for accessing individual scopes from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessScope[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessScope[A]] with HasValues[AccessScopeValue] 
		with FilterScopes[AccessScope[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessScopeValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessScope(newTarget)
}

