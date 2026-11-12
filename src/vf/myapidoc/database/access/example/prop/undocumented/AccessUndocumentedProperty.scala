package vf.myapidoc.database.access.example.prop.undocumented

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.example.UndocumentedProperty

object AccessUndocumentedProperty extends AccessOneRoot[AccessUndocumentedProperty[UndocumentedProperty]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessUndocumentedProperties.root.head
}

/**
  * Used for accessing individual undocumented properties from the DB at a time
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
case class AccessUndocumentedProperty[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessUndocumentedProperty[A]] 
		with HasValues[AccessUndocumentedPropertyValue] 
		with FilterUndocumentedProperties[AccessUndocumentedProperty[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessUndocumentedPropertyValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessUndocumentedProperty(newTarget)
}

