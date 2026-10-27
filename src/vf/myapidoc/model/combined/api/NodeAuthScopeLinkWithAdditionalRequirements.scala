package vf.myapidoc.model.combined.api

import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactoryWrapper
import vf.myapidoc.model.partial.api.NodeAuthScopeLinkData
import vf.myapidoc.model.stored.api.{AdditionalNodeAuthScopeRequirement, NodeAuthScopeLink, StoredNodeAuthLinkLike}

object NodeAuthScopeLinkWithAdditionalRequirements
{
	// OTHER	--------------------
	
	/**
	  * @param link                   The link to wrap
	  * @param additionalRequirements additional requirements to attach
	  * @return Combination of the specified link and additional requirement
	  */
	def apply(link: NodeAuthScopeLink,
		additionalRequirements: Seq[AdditionalNodeAuthScopeRequirement]): NodeAuthScopeLinkWithAdditionalRequirements =
		apply(link.id, link.data, additionalRequirements)
	/**
	  * @param id                     ID of this link in the DB
	  * @param data                   The wrapped link data
	  * @param additionalRequirements additional requirements to attach
	  * @return A new link with the specified additional requirement included
	  */
	def apply(id: Int, data: NodeAuthScopeLinkData, 
		additionalRequirements: Seq[AdditionalNodeAuthScopeRequirement]): NodeAuthScopeLinkWithAdditionalRequirements = 
		_NodeAuthScopeLinkWithAdditionalRequirements(id, data, additionalRequirements)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id                     ID of this link in the DB
	  * @param data                   The wrapped link data
	  * @param additionalRequirements additional requirements to attach
	  */
	private case class _NodeAuthScopeLinkWithAdditionalRequirements(id: Int, data: NodeAuthScopeLinkData,
	                                                                additionalRequirements: Seq[AdditionalNodeAuthScopeRequirement])
		extends NodeAuthScopeLinkWithAdditionalRequirements
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: NodeAuthScopeLinkData) = copy(data = factory)
	}
}

/**
  * Includes possible additional scope requirements with a node-scope link
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthScopeLinkWithAdditionalRequirements 
	extends NodeAuthScopeLink
		with StoredNodeAuthLinkLike[NodeAuthScopeLinkData, NodeAuthScopeLinkWithAdditionalRequirements]
		with NodeAuthScopeLinkFactoryWrapper[NodeAuthScopeLinkData, NodeAuthScopeLinkWithAdditionalRequirements]
{
	// ABSTRACT	--------------------
	
	/**
	  * Additional requirements that are attached to this link
	  */
	def additionalRequirements: Seq[AdditionalNodeAuthScopeRequirement]
	
	
	// IMPLEMENTED  ----------------
	
	override def withAdditionalRequirements(requirements: IterableOnce[AdditionalNodeAuthScopeRequirement]) = {
		val existingAdditionalIds = additionalRequirements.view.map { _.id }.toSet
		if (existingAdditionalIds.isEmpty)
			super.withAdditionalRequirements(requirements)
		else
			super.withAdditionalRequirements(additionalRequirements.view ++
				requirements.iterator.filterNot { r => existingAdditionalIds.contains(r.id) })
	}
}

