package vf.myapidoc.database.access.example.prop

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.example.response.FilterByExampleResponse
import vf.myapidoc.model.stored.example.ExamplePropertyValue

object AccessExamplePropertyValue extends AccessOneRoot[AccessExamplePropertyValue[ExamplePropertyValue]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessExamplePropertyValues.root.head
}

/**
  * Used for accessing individual example property values from the DB at a time
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class AccessExamplePropertyValue[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessExamplePropertyValue[A]] 
		with HasValues[AccessExamplePropertyValueValue] 
		with FilterExamplePropertyValues[AccessExamplePropertyValue[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessExamplePropertyValueValue(wrapped)
	
	lazy val joinResponses = join(MyApiDocTables.exampleResponse)
	lazy val whereResponses = FilterByExampleResponse(joinResponses)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessExamplePropertyValue(newTarget)
}

