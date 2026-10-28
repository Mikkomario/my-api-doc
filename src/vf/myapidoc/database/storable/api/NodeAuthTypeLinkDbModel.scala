package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps
import vf.myapidoc.model.factory.api.NodeAuthTypeLinkFactory
import vf.myapidoc.model.partial.api.NodeAuthTypeLinkData
import vf.myapidoc.model.stored.api.NodeAuthTypeLink

import java.time.Instant

/**
  * Used for constructing NodeAuthTypeLinkDbModel instances and for inserting node auth type 
  * links to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object NodeAuthTypeLinkDbModel 
	extends NodeAuthLinkDbModelFactoryLike[NodeAuthTypeLinkDbModel, NodeAuthTypeLink, NodeAuthTypeLinkData] 
		with NodeAuthTypeLinkFactory[NodeAuthTypeLinkDbModel] with NodeAuthLinkDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with node ids
	  */
	override lazy val nodeId = property("nodeId")
	/**
	  * Database property used for interacting with auth type ids
	  */
	lazy val authTypeId = property("authTypeId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with methods
	  */
	override lazy val method = property("method")
	/**
	  * Database property used for interacting with descriptions
	  */
	override lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def table = MyApiDocTables.nodeAuthTypeLink
	
	override def apply(data: NodeAuthTypeLinkData): NodeAuthTypeLinkDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.nodeId), Some(data.authTypeId), 
			data.removingUpdateId, data.method, data.description)
	
	override def withAuthTypeId(authTypeId: Int) = apply(authTypeId = Some(authTypeId))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withMethod(method: Method) = apply(method = Some(method))
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: NodeAuthTypeLinkData) = NodeAuthTypeLink(id.getInt, data)
}

/**
  * Used for interacting with NodeAuthTypeLinks in the database
  * @param id node auth type link database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class NodeAuthTypeLinkDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                   nodeId: Option[Int] = None, authTypeId: Option[Int] = None,
                                   removingUpdateId: Option[Int] = None, method: Option[Method] = None,
                                   description: String = "")
	extends NodeAuthLinkDbModel with NodeAuthLinkDbModelLike[NodeAuthTypeLinkDbModel] 
		with NodeAuthTypeLinkFactory[NodeAuthTypeLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def dbProps = NodeAuthTypeLinkDbModel
	
	override def table = NodeAuthTypeLinkDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param nodeId           node id to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param authId           auth id to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param method           method to assign to the new model (default = currently assigned value)
	  * @param description      description to assign to the new model (default = currently assigned 
	  *                         value)
	  */
	override def copyNodeAuthLink(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId,
	                              nodeId: Option[Int] = nodeId, authId: Option[Int] = authId,
	                              removingUpdateId: Option[Int] = removingUpdateId, method: Option[Method] = method,
	                              description: String = description) =
		copy(id = id, originalUpdateId = originalUpdateId, nodeId = nodeId, authTypeId = authId, 
			removingUpdateId = removingUpdateId, method = method, description = description)
	
	override def withAuthTypeId(authTypeId: Int) = copy(authTypeId = Some(authTypeId))
}

