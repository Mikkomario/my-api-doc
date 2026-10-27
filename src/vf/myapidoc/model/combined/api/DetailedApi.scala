package vf.myapidoc.model.combined.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.ApiFactoryWrapper
import vf.myapidoc.model.partial.api.ApiData
import vf.myapidoc.model.stored.api.Api
import vf.myapidoc.model.template.ClientModelConvertible

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
	extends Api with ApiFactoryWrapper[ApiData, DetailedApi] with ClientModelConvertible
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
	
	
	// IMPLEMENTED  -----------------------
	
	override def toModel = super.toModel ++ Model.from("models" -> models, "nodeGroups" -> nodeGroups)
	override def toClientModel: Model = Model.from("name" -> data.name, "models" -> models.map { _.toClientModel },
		"nodeGroups" -> nodeGroups.map { _.toClientModel })
	
	override protected def wrap(factory: ApiData): DetailedApi = copy(data = factory)
	override def withId(id: Int): Api = copy(id = id)
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param nodeId ID of the targeted API node
	 * @return Targeted API node. None if that node doesn't appear in this API (version)
	 */
	def node(nodeId: Int) = nodeById.get(nodeId)
}