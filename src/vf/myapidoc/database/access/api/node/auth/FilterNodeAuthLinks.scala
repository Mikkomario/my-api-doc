package vf.myapidoc.database.access.api.node.auth

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

/**
  * Common trait for access points which may be filtered based on node auth link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterNodeAuthLinks[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Model that defines node auth link database properties
	  */
	def model: NodeAuthLinkDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param authId auth id to target
	  * @return Copy of this access point that only includes node auth links with the specified auth id
	  */
	def acceptingAuth(authId: Int) = filter(model.authId.column <=> authId)
	/**
	  * @param authIds Targeted auth ids
	  * @return Copy of this access point that only includes node auth links where auth id is within the 
	  * specified value set
	  */
	def acceptingAuths(authIds: IterableOnce[Int]) = filter(Condition.indexIn(model.authId, authIds))
	
	/**
	  * @param method method to target
	  * @return Copy of this access point that only includes node auth links with the specified method
	  */
	def forMethod(method: Method) = filter(model.method.column <=> method.name)
	/**
	  * @param methods Targeted methods
	  * @return Copy of this access point that only includes node auth links where method is within the 
	  * specified value set
	  */
	def forMethods(methods: Iterable[Method]) = 
		filter(model.method.column.in(methods.map { method => method.name }))
	
	/**
	  * @param nodeId node id to target
	  * @return Copy of this access point that only includes node auth links with the specified node id
	  */
	def ofNode(nodeId: Int) = filter(model.nodeId.column <=> nodeId)
	/**
	  * @param nodeIds Targeted node ids
	  * @return Copy of this access point that only includes node auth links where node id is within the 
	  * specified value set
	  */
	def ofNodes(nodeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.nodeId, nodeIds))
}

