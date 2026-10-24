package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Common trait for access points which may be filtered based on path parameters schema link 
  * properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterPathParameterSchemaLinks[+Repr] 
	extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines path parameters schema link database properties
	  */
	def model = PathParametersSchemaLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param modelTypeId model type id to target
	  * @return Copy of this access point that only includes path parameter schema links with the specified 
	  * model type id
	  */
	def ofModelType(modelTypeId: Int) = filter(model.modelTypeId.column <=> modelTypeId)
	
	/**
	  * @param modelTypeIds Targeted model type ids
	  * @return Copy of this access point that only includes path parameter schema links where model type id 
	  * is within the specified value set
	  */
	def ofModelTypes(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId,
		modelTypeIds))
	
	/**
	  * @param nodeId node id to target
	  * @return Copy of this access point that only includes path parameter schema links with the specified 
	  * node id
	  */
	def ofNode(nodeId: Int) = filter(model.nodeId.column <=> nodeId)
	
	/**
	  * @param nodeIds Targeted node ids
	  * @return Copy of this access point that only includes path parameter schema links where node id is 
	  * within the specified value set
	  */
	def ofNodes(nodeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.nodeId, nodeIds))
}

