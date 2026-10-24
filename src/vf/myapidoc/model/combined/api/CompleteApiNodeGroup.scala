package vf.myapidoc.model.combined.api

import vf.myapidoc.model.factory.api.ApiNodeGroupFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeGroupData
import vf.myapidoc.model.stored.api.{ApiNode, ApiNodeGroup}

object CompleteApiNodeGroup
{
	// OTHER	--------------------
	
	/**
	  * @param group The group to wrap
	  * @param nodes nodes to attach
	  * @return Combination of the specified group and node
	  */
	def apply(group: ApiNodeGroup, nodes: Seq[ApiNode]): CompleteApiNodeGroup = apply(group.id, group.data, nodes)
	/**
	  * @param id    ID of this group in the DB
	  * @param data  The wrapped group data
	  * @param nodes nodes to attach
	  * @return A new group with the specified node included
	  */
	def apply(id: Int, data: ApiNodeGroupData, nodes: Seq[ApiNode]): CompleteApiNodeGroup = 
		_CompleteApiNodeGroup(id, data, nodes)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id    ID of this group in the DB
	  * @param data  The wrapped group data
	  * @param nodes nodes to attach
	  */
	private case class _CompleteApiNodeGroup(id: Int, data: ApiNodeGroupData, nodes: Seq[ApiNode]) 
		extends CompleteApiNodeGroup
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: ApiNodeGroupData) = copy(data = factory)
	}
}

/**
  * Includes the grouped API nodes
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait CompleteApiNodeGroup 
	extends ApiNodeGroup with ApiNodeGroupFactoryWrapper[ApiNodeGroupData, CompleteApiNodeGroup]
{
	// ABSTRACT	--------------------
	
	/**
	  * Nodes that are attached to this group
	  */
	def nodes: Seq[ApiNode]
}

