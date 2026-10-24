package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps
import vf.myapidoc.model.factory.api.NodeAuthScopeLinkFactory
import vf.myapidoc.model.partial.api.NodeAuthScopeLinkData
import vf.myapidoc.model.stored.api.NodeAuthScopeLink

import java.time.Instant

/**
  * Used for constructing NodeAuthScopeLinkDbModel instances and for inserting node auth scope 
  * links to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object NodeAuthScopeLinkDbModel 
	extends NodeAuthLinkDbModelFactoryLike[NodeAuthScopeLinkDbModel, NodeAuthScopeLink, NodeAuthScopeLinkData] 
		with NodeAuthScopeLinkFactory[NodeAuthScopeLinkDbModel] with NodeAuthLinkDbProps 
		with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with node ids
	  */
	override lazy val nodeId = property("nodeId")
	
	/**
	  * Database property used for interacting with scope ids
	  */
	lazy val scopeId = property("scopeId")
	
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
	
	override def authId = scopeId
	
	override def table = MyApiDocTables.nodeAuthScopeLink
	
	override def apply(data: NodeAuthScopeLinkData): NodeAuthScopeLinkDbModel = 
		apply(None, Some(data.nodeId), Some(data.scopeId), data.method, data.description, Some(data.created), 
			data.deprecatedAfter)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withMethod(method: Method) = apply(method = Some(method))
	
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	
	override def withScopeId(scopeId: Int) = apply(scopeId = Some(scopeId))
	
	override protected def complete(id: Value, data: NodeAuthScopeLinkData) = NodeAuthScopeLink(id.getInt, 
		data)
}

/**
  * Used for interacting with NodeAuthScopeLinks in the database
  * @param id node auth scope link database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class NodeAuthScopeLinkDbModel(id: Option[Int] = None, nodeId: Option[Int] = None, 
	scopeId: Option[Int] = None, method: Option[Method] = None, description: String = "", 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends NodeAuthLinkDbModel with NodeAuthLinkDbModelLike[NodeAuthScopeLinkDbModel] 
		with NodeAuthScopeLinkFactory[NodeAuthScopeLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def authId = scopeId
	
	override def dbProps = NodeAuthScopeLinkDbModel
	
	override def table = NodeAuthScopeLinkDbModel.table
	
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
		copy(id = id, nodeId = nodeId, scopeId = authId, method = method, description = description, 
			created = created, deprecatedAfter = deprecatedAfter)
	
	override def withScopeId(scopeId: Int) = copy(scopeId = Some(scopeId))
}

