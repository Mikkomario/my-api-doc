package vf.myapidoc.database.access.api.node.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.storable.api.ApiNodePathDbModel

/**
  * Common trait for access points that may be filtered based on api node path properties
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait FilterApiNodePaths[+Repr] extends FilterVersionedDocElements[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines api node path database properties
	  */
	def model = ApiNodePathDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param paramsModelSchemaIds Targeted params model schema ids
	  * @return Copy of this access point that only includes api node paths where params model schema id is 
	  * within the specified value set
	  */
	def acceptingAnyOfParams(paramsModelSchemaIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.paramsModelSchemaId, paramsModelSchemaIds))
	/**
	  * @param paramsModelSchemaId params model schema id to target
	  * @return Copy of this access point that only includes api node paths with the specified params model 
	  * schema id
	  */
	def acceptingParams(paramsModelSchemaId: Int) = 
		filter(model.paramsModelSchemaId.column <=> paramsModelSchemaId)
	
	/**
	  * @param nodeId node id to target
	  * @return Copy of this access point that only includes api node paths with the specified node id
	  */
	def ofNode(nodeId: Int) = filter(model.nodeId.column <=> nodeId)
	/**
	  * @param nodeIds Targeted node ids
	  * @return Copy of this access point that only includes api node paths where node id is within the 
	  * specified value set
	  */
	def ofNodes(nodeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.nodeId, nodeIds))
	
	/**
	  * @param relative relative to target
	  * @return Copy of this access point that only includes api node paths with the specified relative
	  */
	def withRelative(relative: String) = filter(model.relative.column <=> relative)
	/**
	  * @param relatives Targeted relatives
	  * @return Copy of this access point that only includes api node paths where relative is within the 
	  * specified value set
	  */
	def withRelatives(relatives: Iterable[String]) = filter(model.relative.column.in(relatives))
}

