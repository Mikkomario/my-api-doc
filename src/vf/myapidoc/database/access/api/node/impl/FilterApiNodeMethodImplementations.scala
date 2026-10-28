package vf.myapidoc.database.access.api.node.impl

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.Column
import utopia.vault.nosql.template.IntIndexFilterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel

/**
  * Common trait for access points that may be filtered based on api node method implementation 
  * properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterApiNodeMethodImplementations[+Repr] extends FilterVersionedDocElements[Repr] with IntIndexFilterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines api node method implementation database properties
	  */
	def model = ApiNodeMethodImplementationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override protected def idColumn: Column = model.id
	
	
	// OTHER	--------------------
	
	/**
	  * @param method method to target
	  * @return Copy of this access point that only includes api node method implementations with the 
	  * specified method
	  */
	def ofMethod(method: Method) = filter(model.method.column <=> method.name)
	/**
	  * @param methods Targeted methods
	  * @return Copy of this access point that only includes api node method implementations where method is 
	  * within the specified value set
	  */
	def ofMethods(methods: Iterable[Method]) = filter(model.method.column.in(methods.map { method => method.name }))
	
	/**
	  * @param nodeId node id to target
	  * @return Copy of this access point that only includes api node method implementations with the 
	  * specified node id
	  */
	def ofNode(nodeId: Int) = filter(model.nodeId.column <=> nodeId)
	/**
	  * @param nodeIds Targeted node ids
	  * @return Copy of this access point that only includes api node method implementations where node id is 
	  * within the specified value set
	  */
	def ofNodes(nodeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.nodeId, nodeIds))
}

