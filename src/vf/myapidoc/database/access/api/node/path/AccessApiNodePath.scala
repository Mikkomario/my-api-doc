package vf.myapidoc.database.access.api.node.path

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ApiNodePath

object AccessApiNodePath extends AccessOneDeprecatingRoot[AccessApiNodePath[ApiNodePath]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessApiNodePaths.all.head
}

/**
  * Used for accessing individual api node paths from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class AccessApiNodePath[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessApiNodePath[A]] with HasValues[AccessApiNodePathValue] 
		with FilterApiNodePaths[AccessApiNodePath[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodePathValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessApiNodePath(newTarget)
}

