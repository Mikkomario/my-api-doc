package vf.myapidoc.model.cached

import utopia.flow.collection.immutable.Empty

/**
 * Represents a group of API nodes not yet stored.
 * @param name Name of this node group
 * @param nodes Nodes that belong to this group
 * @param description A description of this group
 * @author Mikko Hilpinen
 * @since 21.10.2026, v0.1
 */
case class NewApiNodeGroup(name: String, nodes: Seq[NewApiNode] = Empty, description: String = "")
{
	def tokenTypeNamesIterator = nodes.iterator.flatMap { _.tokenTypeNamesIterator }
	def scopeNamesIterator = nodes.iterator.flatMap { _.scopeNamesIterator }
}