package vf.myapidoc.model.combined.api

import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.partial.api.ApiData

/**
 * Includes full API documentation
 * @param id ID of this API
 * @param data Wrapped API data
 * @param nodeGroups Grouped API node information
 * @param models All named models defined in this API
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedApi(id: Int, data: ApiData, nodeGroups: Seq[DetailedApiNodeGroup], models: Seq[DetailedModelSchema])
{
	// ATTRIBUTES -------------------------
	
	/**
	 * A map where keys are node IDs and values are the matching nodes
	 */
	lazy val nodeById = nodesIterator.map { n => n.id -> n }.toMap
	
	
	// COMPUTED ---------------------------
	
	/**
	 * @return An iterator that yields all nodes in this API
	 */
	def nodesIterator = nodeGroups.iterator.flatMap { _.nodesIterator }
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param nodeId ID of the targeted API node
	 * @return Targeted API node. None if that node doesn't appear in this API (version)
	 */
	def node(nodeId: Int) = nodeById.get(nodeId)
}