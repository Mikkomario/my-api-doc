package vf.myapidoc.database.access.api.response.status

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.PossibleResponseStatus

object AccessPossibleResponseStatus 
	extends AccessOneDeprecatingRoot[AccessPossibleResponseStatus[PossibleResponseStatus]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessPossibleResponseStatuses.all.head
}

/**
  * Used for accessing individual possible response statuses from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessPossibleResponseStatus[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessPossibleResponseStatus[A]] 
		with HasValues[AccessPossibleResponseStatusValue] 
		with FilterPossibleResponseStatuses[AccessPossibleResponseStatus[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPossibleResponseStatusValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessPossibleResponseStatus(newTarget)
}

