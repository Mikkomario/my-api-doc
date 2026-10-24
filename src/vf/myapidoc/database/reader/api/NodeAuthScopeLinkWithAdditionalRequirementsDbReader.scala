package vf.myapidoc.database.reader.api

import utopia.vault.nosql.read.linked.MultiLinkedDbReader
import vf.myapidoc.model.combined.api.NodeAuthScopeLinkWithAdditionalRequirements
import vf.myapidoc.model.stored.api.{AdditionalNodeAuthScopeRequirement, NodeAuthScopeLink}

/**
  * Used for reading node auth scope links with additional requirements from the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object NodeAuthScopeLinkWithAdditionalRequirementsDbReader 
	extends MultiLinkedDbReader[NodeAuthScopeLink, AdditionalNodeAuthScopeRequirement, 
		NodeAuthScopeLinkWithAdditionalRequirements](NodeAuthScopeLinkDbReader, 
		AdditionalNodeAuthScopeRequirementDbReader, neverEmptyRight = false)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param link                   link to wrap
	  * @param additionalRequirements additional requirements to attach
	  */
	override def combine(link: NodeAuthScopeLink, 
		additionalRequirements: Seq[AdditionalNodeAuthScopeRequirement]) = 
		NodeAuthScopeLinkWithAdditionalRequirements(link, additionalRequirements)
}

