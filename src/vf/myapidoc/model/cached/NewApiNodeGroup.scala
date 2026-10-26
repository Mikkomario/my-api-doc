package vf.myapidoc.model.cached

import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

/**
 * Represents a group of API nodes not yet stored.
 * @param name Name of this node group
 * @param nodes Nodes that belong to this group
 * @param description A description of this group
 * @author Mikko Hilpinen
 * @since 21.10.2026, v0.1
 */
case class NewApiNodeGroup(name: String, nodes: Seq[NewApiNode] = Empty, description: String = "")
	extends ModelConvertible
{
	// COMPUTED -------------------------
	
	def tokenTypeNamesIterator = nodes.iterator.flatMap { _.tokenTypeNamesIterator }
	def scopeNamesIterator = nodes.iterator.flatMap { _.scopeNamesIterator }
	
	
	// IMPLEMENTED  ---------------------
	
	override def toModel: Model = Model.from("name" -> name, "description" -> description, "nodes" -> nodes)
}