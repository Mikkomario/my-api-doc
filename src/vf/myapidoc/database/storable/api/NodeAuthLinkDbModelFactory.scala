package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.props.api.{NodeAuthLinkDbProps, NodeAuthLinkDbPropsWrapper}
import vf.myapidoc.model.partial.api.NodeAuthLinkData
import vf.myapidoc.model.stored.api.NodeAuthLink

import java.time.Instant

object NodeAuthLinkDbModelFactory
{
	// OTHER	--------------------
	
	/**
	  * @return A factory for constructing node auth link database models
	  */
	def apply(table: Table, dbProps: NodeAuthLinkDbProps) = NodeAuthLinkDbModelFactoryImpl(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * Used for constructing NodeAuthLinkDbModel instances and for inserting node auth links to the 
	  * database
	  * @param table               Table targeted by these models
	  * @param nodeAuthLinkDbProps Properties that specify how the database interactions are performed
	  * @author Mikko Hilpinen
	  * @since 22.10.2026, v0.1
	  */
	case class NodeAuthLinkDbModelFactoryImpl(table: Table, nodeAuthLinkDbProps: NodeAuthLinkDbProps) 
		extends NodeAuthLinkDbModelFactory with NodeAuthLinkDbPropsWrapper with DeprecatesAfterDefined
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		
		// IMPLEMENTED	--------------------
		
		override def apply(data: NodeAuthLinkData): NodeAuthLinkDbModel = 
			apply(None, Some(data.originalUpdateId), Some(data.nodeId), Some(data.authId), 
				data.removingUpdateId, data.method, data.description)
		
		override def withAuthId(authId: Int) = apply(authId = Some(authId))
		override def withDescription(description: String) = apply(description = description)
		override def withId(id: Int) = apply(id = Some(id))
		override def withMethod(method: Method) = apply(method = Some(method))
		override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
		override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
		override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
		
		override protected def complete(id: Value, data: NodeAuthLinkData) = NodeAuthLink(id.getInt, data)
		
		
		// OTHER	--------------------
		
		/**
		  * @param id node auth link database id
		  * @return Constructs a new node auth link database model with the specified properties
		  */
		def apply(id: Option[Int] = None, originalUpdateId: Option[Int] = None, nodeId: Option[Int] = None,
		          authId: Option[Int] = None, removingUpdateId: Option[Int] = None, method: Option[Method] = None,
		          description: String = ""): NodeAuthLinkDbModel =
			_NodeAuthLinkDbModel(table, nodeAuthLinkDbProps, id, originalUpdateId, nodeId, authId, 
				removingUpdateId, method, description)
	}
	
	/**
	  * Used for interacting with NodeAuthLinks in the database
	  * @param table   Table interacted with when using this model
	  * @param dbProps Configurations of the interacted database properties
	  * @param id      node auth link database id
	  * @author Mikko Hilpinen
	  * @since 22.10.2026, v0.1
	  */
	private case class _NodeAuthLinkDbModel(table: Table, dbProps: NodeAuthLinkDbProps, id: Option[Int] = None,
	                                        originalUpdateId: Option[Int] = None, nodeId: Option[Int] = None,
	                                        authId: Option[Int] = None, removingUpdateId: Option[Int] = None,
	                                        method: Option[Method] = None, description: String = "")
		extends NodeAuthLinkDbModel
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param id               ID to assign to the new model (default = currently assigned id)
		  * @param originalUpdateId original update id to assign to the new model (default = currently 
		  *                         assigned value)
		  * @param removingUpdateId removing update id to assign to the new model (default = currently 
		  *                         assigned value)
		  */
		override def copyVersionedDocElement(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId,
		                                     removingUpdateId: Option[Int] = removingUpdateId) =
			copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
		
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
		  * @return Copy of this model with the specified node auth link properties
		  */
		override protected def copyNodeAuthLink(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId,
		                                        nodeId: Option[Int] = nodeId, authId: Option[Int] = authId,
		                                        removingUpdateId: Option[Int] = removingUpdateId,
		                                        method: Option[Method] = method, description: String = description) =
			copy(id = id, originalUpdateId = originalUpdateId, nodeId = nodeId, authId = authId, 
				removingUpdateId = removingUpdateId, method = method, description = description)
	}
}

/**
  * Common trait for factories yielding node auth link database models
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbModelFactory 
	extends NodeAuthLinkDbModelFactoryLike[NodeAuthLinkDbModel, NodeAuthLink, NodeAuthLinkData]

