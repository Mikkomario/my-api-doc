package vf.myapidoc.database.access.test.url

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.test.TestUrl

object AccessTestUrl extends AccessOneRoot[AccessTestUrl[TestUrl]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessTestUrls.root.head
}

/**
  * Used for accessing individual test urls from the DB at a time
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestUrl[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessTestUrl[A]] with HasValues[AccessTestUrlValue] 
		with FilterTestUrls[AccessTestUrl[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessTestUrlValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessTestUrl(newTarget)
}

