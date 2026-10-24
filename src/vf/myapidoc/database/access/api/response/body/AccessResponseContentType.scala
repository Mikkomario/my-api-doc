package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ResponseContentType

object AccessResponseContentType 
	extends AccessOneDeprecatingRoot[AccessResponseContentType[ResponseContentType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessResponseContentTypes.all.head
}

/**
  * Used for accessing individual response content types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessResponseContentType[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessResponseContentType[A]] 
		with HasValues[AccessResponseContentTypeValue] 
		with FilterResponseContentTypes[AccessResponseContentType[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessResponseContentTypeValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessResponseContentType(newTarget)
}

