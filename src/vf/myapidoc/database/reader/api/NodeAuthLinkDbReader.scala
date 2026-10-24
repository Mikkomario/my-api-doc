package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps
import vf.myapidoc.model.partial.api.NodeAuthLinkData
import vf.myapidoc.model.stored.api.NodeAuthLink

import java.time.Instant

object NodeAuthLinkDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing node auth links from database model data
	  */
	def apply(table: Table, dbProps: NodeAuthLinkDbProps): NodeAuthLinkDbReader = 
		_NodeAuthLinkDbReader(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _NodeAuthLinkDbReader(table: Table, dbProps: NodeAuthLinkDbProps) 
		extends NodeAuthLinkDbReader
	{
		// IMPLEMENTED	--------------------
		
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
			method: Option[Method], description: String, created: Instant, 
			deprecatedAfter: Option[Instant]) = 
			NodeAuthLink(id, NodeAuthLinkData(nodeId, authId, method, description, created, deprecatedAfter))
	}
}

/**
  * Common trait for factories which parse node auth link data from database-originated models
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbReader extends NodeAuthLinkDbReaderLike[NodeAuthLink]

