package vf.myapidoc.database.access.example.response

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.example.ExampleResponse

object AccessExampleResponse extends AccessOneRoot[AccessExampleResponse[ExampleResponse]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessExampleResponses.root.head
}

/**
  * Used for accessing individual example responses from the DB at a time
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExampleResponse[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessExampleResponse[A]] with HasValues[AccessExampleResponseValue] 
		with FilterExampleResponses[AccessExampleResponse[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessExampleResponseValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessExampleResponse(newTarget)
}

