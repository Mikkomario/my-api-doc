package vf.myapidoc.database.access.api.update

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.DocUpdate

object AccessDocUpdate extends AccessOneRoot[AccessDocUpdate[DocUpdate]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessDocUpdates.root.head
}

/**
  * Used for accessing individual doc updates from the DB at a time
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class AccessDocUpdate[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessDocUpdate[A]] with HasValues[AccessDocUpdateValue] 
		with FilterDocUpdates[AccessDocUpdate[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessDocUpdateValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessDocUpdate(newTarget)
}

