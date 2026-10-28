package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps
import vf.myapidoc.model.partial.api.NodeAuthLinkData
import vf.myapidoc.model.stored.api.NodeAuthLink

object NodeAuthLinkDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing node auth links from database model data
	  */
	def apply(dbProps: NodeAuthLinkDbProps): NodeAuthLinkDbReader = _NodeAuthLinkDbReader(dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _NodeAuthLinkDbReader(dbProps: NodeAuthLinkDbProps) extends NodeAuthLinkDbReader
	{
		// IMPLEMENTED	--------------------
		
		override def table = dbProps.table
		
		/**
		  * @param model            Model from which additional data may be read
		  * @param id               ID to assign to the read/parsed node auth link
		  * @param originalUpdateId original update id to assign to the new node auth link
		  * @param nodeId           node id to assign to the new node auth link
		  * @param authId           auth id to assign to the new node auth link
		  * @param removingUpdateId removing update id to assign to the new node auth link
		  * @param method           method to assign to the new node auth link
		  * @param description      description to assign to the new node auth link
		  */
		override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, nodeId: Int, 
			authId: Int, removingUpdateId: Option[Int], method: Option[Method], description: String) = 
			NodeAuthLink(id, NodeAuthLinkData(originalUpdateId, nodeId, authId, removingUpdateId, method, 
				description))
	}
}

/**
  * Common trait for factories that parse node auth link data from database-originated models
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbReader extends NodeAuthLinkDbReaderLike[NodeAuthLink]

