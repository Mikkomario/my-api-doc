package vf.myapidoc.database.access.api

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.Api

object AccessApi extends AccessOneRoot[AccessApi[Api]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessApis.root.head
}

/**
  * Used for accessing individual apis from the DB at a time
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class AccessApi[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessApi[A]] with HasValues[AccessApiValue] 
		with FilterApis[AccessApi[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessApi(newTarget)
}

