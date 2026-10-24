package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.NodeAuthScopeLinkDbModel
import vf.myapidoc.model.partial.api.NodeAuthScopeLinkData
import vf.myapidoc.model.stored.api.NodeAuthScopeLink

import java.time.Instant

/**
  * Used for reading node auth scope link data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object NodeAuthScopeLinkDbReader extends NodeAuthLinkDbReaderLike[NodeAuthScopeLink]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = NodeAuthScopeLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model           Model from which additional data may be read
	  * @param id              Id to assign to the read/parsed node auth link
	  * @param nodeId          node id to assign to the new node auth link
	  * @param authId          auth id to assign to the new node auth link
	  * @param method          method to assign to the new node auth link
	  * @param description     description to assign to the new node auth link
	  * @param created         created to assign to the new node auth link
	  * @param deprecatedAfter deprecated after to assign to the new node auth link
	  */
	override protected def apply(model: HasProperties, id: Int, nodeId: Int, authId: Int, 
		method: Option[Method], description: String, created: Instant, deprecatedAfter: Option[Instant]) = 
		NodeAuthScopeLink(id, NodeAuthScopeLinkData(nodeId = nodeId, scopeId = authId, method = method, 
			description = description, created = created, deprecatedAfter = deprecatedAfter))
}

