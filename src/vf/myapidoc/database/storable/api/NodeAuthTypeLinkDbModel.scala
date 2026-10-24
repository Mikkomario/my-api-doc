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
		with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with node ids
	  */
	override lazy val nodeId = property("nodeId")
	
	/**
	  * Database property used for interacting with auth type ids
	  */
	lazy val authTypeId = property("authTypeId")
	
	/**
	  * Database property used for interacting with methods
	  */
	override lazy val method = property("method")
	
	/**
	  * Database property used for interacting with descriptions
	  */
	override lazy val description = property("description")
	
	/**
	  * Database property used for interacting with creation times
	  */
	override lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	override lazy val deprecatedAfter = property("deprecatedAfter")
	
	
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def table = MyApiDocTables.nodeAuthTypeLink
	
	override def apply(data: NodeAuthTypeLinkData): NodeAuthTypeLinkDbModel = 
		apply(None, Some(data.nodeId), Some(data.authTypeId), data.method, data.description, 
			Some(data.created), data.deprecatedAfter)
	
	override def withAuthTypeId(authTypeId: Int) = apply(authTypeId = Some(authTypeId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withMethod(method: Method) = apply(method = Some(method))
	
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	
	override protected def complete(id: Value, data: NodeAuthTypeLinkData) = NodeAuthTypeLink(id.getInt, data)
}

/**
  * Used for interacting with NodeAuthTypeLinks in the database
  * @param id node auth type link database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class NodeAuthTypeLinkDbModel(id: Option[Int] = None, nodeId: Option[Int] = None, 
	authTypeId: Option[Int] = None, method: Option[Method] = None, description: String = "", 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends NodeAuthLinkDbModel with NodeAuthLinkDbModelLike[NodeAuthTypeLinkDbModel] 
		with NodeAuthTypeLinkFactory[NodeAuthTypeLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def authId = authTypeId
	
	override def dbProps = NodeAuthTypeLinkDbModel
	
	override def table = NodeAuthTypeLinkDbModel.table
	
	/**
	  * @param id              Id to assign to the new model (default = currently assigned id)
	  * @param nodeId          node id to assign to the new model (default = currently assigned value)
	  * @param authId          auth id to assign to the new model (default = currently assigned value)
	  * @param method          method to assign to the new model (default = currently assigned value)
	  * @param description     description to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param created         created to assign to the new model (default = currently assigned value)
	  * @param deprecatedAfter deprecated after to assign to the new model (default = currently 
	  *                        assigned value)
	  */
	override def copyNodeAuthLink(id: Option[Int] = id, nodeId: Option[Int] = nodeId, 
		authId: Option[Int] = authId, method: Option[Method] = method, description: String = description, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter) = 
		copy(id = id, nodeId = nodeId, authTypeId = authId, method = method, description = description, 
			created = created, deprecatedAfter = deprecatedAfter)
	
	override def withAuthTypeId(authTypeId: Int) = copy(authTypeId = Some(authTypeId))
}

