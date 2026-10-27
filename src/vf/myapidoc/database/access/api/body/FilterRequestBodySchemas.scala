package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.node.impl.link.model.FilterApiNodeMethodImplementationModelSchemaLinks
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel

/**
  * Common trait for access points which may be filtered based on request body schema properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterRequestBodySchemas[+Repr] extends FilterBodySchemas[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines request body schema database properties
	  */
	def model = RequestBodySchemaDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param implementationId implementation id to target
	  * @return Copy of this access point that only includes request body schemas with the specified 
	  * implementation id
	  */
	def ofImplementation(implementationId: Int) = filter(model.implementationId.column <=> implementationId)
	/**
	  * @param implementationIds Targeted implementation ids
	  * @return Copy of this access point that only includes request body schemas where implementation id is 
	  * within the specified value set
	  */
	def ofImplementations(implementationIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.implementationId, implementationIds))
}

