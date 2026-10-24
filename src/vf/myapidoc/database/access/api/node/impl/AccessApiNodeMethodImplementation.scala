package vf.myapidoc.database.access.api.node.impl

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementation

object AccessApiNodeMethodImplementation 
	extends AccessOneDeprecatingRoot[AccessApiNodeMethodImplementation[ApiNodeMethodImplementation]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessApiNodeMethodImplementations.all.head
}

/**
  * Used for accessing individual api node method implementations from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessApiNodeMethodImplementation[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessApiNodeMethodImplementation[A]] 
		with HasValues[AccessApiNodeMethodImplementationValue] 
		with FilterApiNodeMethodImplementations[AccessApiNodeMethodImplementation[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeMethodImplementationValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = 
		AccessApiNodeMethodImplementation(newTarget)
}

