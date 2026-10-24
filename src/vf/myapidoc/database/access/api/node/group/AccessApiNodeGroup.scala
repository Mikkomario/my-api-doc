package vf.myapidoc.database.access.api.node.group

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ApiNodeGroup

object AccessApiNodeGroup 
	extends AccessOneDeprecatingRoot[AccessApiNodeGroup[ApiNodeGroup]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessApiNodeGroups.all.head
}

/**
  * Used for accessing individual api node groups from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroup[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessApiNodeGroup[A]] with HasValues[AccessApiNodeGroupValue] 
		with FilterApiNodeGroups[AccessApiNodeGroup[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessApiNodeGroupValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessApiNodeGroup(newTarget)
}

