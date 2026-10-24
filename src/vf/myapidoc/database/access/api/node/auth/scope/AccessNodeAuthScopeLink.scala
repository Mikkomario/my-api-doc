package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.NodeAuthScopeLink

object AccessNodeAuthScopeLink extends AccessOneDeprecatingRoot[AccessNodeAuthScopeLink[NodeAuthScopeLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessNodeAuthScopeLinks.all.head
}

/**
  * Used for accessing individual node auth scope links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthScopeLink[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessNodeAuthScopeLink[A]] 
		with HasValues[AccessNodeAuthScopeLinkValue] with FilterNodeAuthScopeLinks[AccessNodeAuthScopeLink[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessNodeAuthScopeLinkValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessNodeAuthScopeLink(newTarget)
}

