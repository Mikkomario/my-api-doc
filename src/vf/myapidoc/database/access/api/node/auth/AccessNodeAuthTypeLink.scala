package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.NodeAuthTypeLink

object AccessNodeAuthTypeLink extends AccessOneDeprecatingRoot[AccessNodeAuthTypeLink[NodeAuthTypeLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessNodeAuthTypeLinks.all.head
}

/**
  * Used for accessing individual node auth type links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthTypeLink[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessNodeAuthTypeLink[A]] 
		with HasValues[AccessNodeAuthTypeLinkValue] with FilterNodeAuthTypeLinks[AccessNodeAuthTypeLink[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessNodeAuthTypeLinkValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessNodeAuthTypeLink(newTarget)
}

