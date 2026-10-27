package vf.myapidoc.database.access.api.response.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.body.FilterBodySchemas
import vf.myapidoc.database.storable.api.ResponseBodySchemaDbModel

/**
  * Common trait for access points which may be filtered based on response body schema properties
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait FilterResponseBodySchemas[+Repr] extends FilterBodySchemas[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines response body schema database properties
	  */
	def model = ResponseBodySchemaDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param statusId status id to target
	  * @return Copy of this access point that only includes response body schemas with the specified status 
	  * id
	  */
	def withStatus(statusId: Int) = filter(model.statusId.column <=> statusId)
	
	/**
	  * @param statusIds Targeted status ids
	  * @return Copy of this access point that only includes response body schemas where status id is within 
	  * the specified value set
	  */
	def withStatuses(statusIds: IterableOnce[Int]) = filter(Condition.indexIn(model.statusId, statusIds))
}

