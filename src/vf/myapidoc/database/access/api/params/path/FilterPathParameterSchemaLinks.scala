package vf.myapidoc.database.access.api.params.path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel

/**
  * Common trait for access points that may be filtered based on path parameters schema link 
  * properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
@deprecated("Replaced with FilterApiNodePaths", "v0.1")
trait FilterPathParameterSchemaLinks[+Repr] extends FilterVersionedDocElements[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines path parameters schema link database properties
	  */
	def model = PathParametersSchemaLinkDbModel
	
	
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
	def ofModelTypes(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId, modelTypeIds))
	
	/**
	  * @param pathId path id to target
	  * @return Copy of this access point that only includes path parameter schema links with the specified 
	  * path id
	  */
	def onPath(pathId: Int) = filter(model.pathId.column <=> pathId)
	/**
	  * @param pathIds Targeted path ids
	  * @return Copy of this access point that only includes path parameter schema links where path id is 
	  * within the specified value set
	  */
	def onPaths(pathIds: IterableOnce[Int]) = filter(Condition.indexIn(model.pathId, pathIds))
}

