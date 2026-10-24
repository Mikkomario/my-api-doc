package vf.myapidoc.database.access.api.response.status

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.response.body.{AccessResponseContentTypeValue, FilterByResponseContentType}
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
	
	/**
	  * A copy of this access which also targets response_content_type
	  */
	lazy val joinBody = join(MyApiDocTables.responseContentType)
	
	/**
	  * Access to the values of linked response content type
	  */
	lazy val body = AccessResponseContentTypeValue(joinBody)
	
	/**
	  * Access to response content type -based filtering functions
	  */
	lazy val whereBody = FilterByResponseContentType(joinBody)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessPossibleResponseStatus(newTarget)
}

