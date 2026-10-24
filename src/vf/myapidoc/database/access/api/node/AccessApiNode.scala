package vf.myapidoc.database.access.api.node

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ApiNode

object AccessApiNode 
	extends AccessOneDeprecatingRoot[AccessApiNode[ApiNode]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessApiNodes.all.head
}

/**
  * Used for accessing individual api nodes from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNode[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessApiNode[A]] with HasValues[AccessApiNodeValue] 
		with FilterApiNodes[AccessApiNode[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessApiNode(newTarget)
}

