package vf.myapidoc.database.access.api.params.query

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.node.impl.link.model.FilterApiNodeMethodImplementationModelSchemaLinks
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel

/**
  * Common trait for access points that may be filtered based on query parameters schema link 
  * properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterQueryParameterSchemaLinks[+Repr] extends FilterVersionedDocElements[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines query parameters schema link database properties
	  */
	def model = QueryParametersSchemaLinkDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param implementationId implementation id to target
	  * @return Copy of this access point that only includes query parameter schema links with the specified 
	  * implementation id
	  */
	def ofImplementation(implementationId: Int) = filter(model.implementationId.column <=> implementationId)
	/**
	  * @param implementationIds Targeted implementation ids
	  * @return Copy of this access point that only includes query parameter schema links where 
	  * implementation id is within the specified value set
	  */
	def ofImplementations(implementationIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.implementationId, implementationIds))
	
	/**
	  * @param modelTypeId model type id to target
	  * @return Copy of this access point that only includes query parameter schema links with the specified 
	  * model type id
	  */
	def ofModelType(modelTypeId: Int) = filter(model.modelTypeId.column <=> modelTypeId)
	/**
	  * @param modelTypeIds Targeted model type ids
	  * @return Copy of this access point that only includes query parameter schema links where model type id 
	  * is within the specified value set
	  */
	def ofModelTypes(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId, 
		modelTypeIds))
}

