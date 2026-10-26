package vf.myapidoc.database.access.schema.prop.value

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.schema.PossiblePropertyValue

object AccessPossiblePropertyValue 
	extends AccessOneDeprecatingRoot[AccessPossiblePropertyValue[PossiblePropertyValue]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessPossiblePropertyValues.all.head
}

/**
  * Used for accessing individual possible property values from the DB at a time
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class AccessPossiblePropertyValue[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessPossiblePropertyValue[A]] 
		with HasValues[AccessPossiblePropertyValueValue] 
		with FilterPossiblePropertyValues[AccessPossiblePropertyValue[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPossiblePropertyValueValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessPossiblePropertyValue(newTarget)
}

