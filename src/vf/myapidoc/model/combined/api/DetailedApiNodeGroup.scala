package vf.myapidoc.model.combined.api

import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.generic.model.immutable.Constant
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.model.factory.api.ApiNodeGroupFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeGroupData
import vf.myapidoc.model.stored.api.ApiNodeGroup

/**
 * Includes full API node information with an API node group
 * @param id ID of this group
 * @param data Wrapped group data
 * @param rootNodes API nodes that appear directly under the API root, as trees.
 *                  Other nodes are nested inside these trees.
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedApiNodeGroup(id: Int, data: ApiNodeGroupData, rootNodes: Seq[ValueTree[DetailedApiNode]])
	extends ApiNodeGroup with ApiNodeGroupFactoryWrapper[ApiNodeGroupData, DetailedApiNodeGroup]
{
	// COMPUTED ---------------------------
	
	/**
	 * @return An iterator that yields all API nodes in this group
	 */
	def nodesIterator = rootNodes.iterator.flatMap { _.valuesIterator }
	
	
	// IMPLEMENTED  -----------------------
	
	override def toModel = super.toModel + Constant("nodes" -> rootNodes.flatMap { _.valuesIterator })
	
	override protected def wrap(factory: ApiNodeGroupData): DetailedApiNodeGroup = copy(data = factory)
	override def withId(id: Int): ApiNodeGroup = copy(id = id)
}
