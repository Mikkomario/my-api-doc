package vf.myapidoc.database.access.schema.prop.types

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.schema.AlternativePropertyType

object AccessAlternativePropertyType 
	extends AccessOneDeprecatingRoot[AccessAlternativePropertyType[AlternativePropertyType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessAlternativePropertyTypes.all.head
}

/**
  * Used for accessing individual alternative property types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessAlternativePropertyType[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessAlternativePropertyType[A]] 
		with HasValues[AccessAlternativePropertyTypeValue] 
		with FilterAlternativePropertyTypes[AccessAlternativePropertyType[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAlternativePropertyTypeValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessAlternativePropertyType(newTarget)
}

