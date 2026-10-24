package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.AdditionalNodeAuthScopeRequirement

object AccessAdditionalNodeAuthScopeRequirement 
	extends AccessOneRoot[AccessAdditionalNodeAuthScopeRequirement[AdditionalNodeAuthScopeRequirement]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessAdditionalNodeAuthScopeRequirements.root.head
}

/**
  * Used for accessing individual additional node auth scope requirements from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessAdditionalNodeAuthScopeRequirement[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessAdditionalNodeAuthScopeRequirement[A]] 
		with HasValues[AccessAdditionalNodeAuthScopeRequirementValue] 
		with FilterAdditionalNodeAuthScopeRequirements[AccessAdditionalNodeAuthScopeRequirement[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessAdditionalNodeAuthScopeRequirementValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = 
		AccessAdditionalNodeAuthScopeRequirement(newTarget)
}

