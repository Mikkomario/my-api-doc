package vf.myapidoc.database.access.api.response.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.ResponseContentTypeDbModel

/**
  * Common trait for access points which may be filtered based on response content type properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterResponseContentTypes[+Repr] 
	extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines response content type database properties
	  */
	def model = ResponseContentTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param statusId status id to target
	  * @return Copy of this access point that only includes response content types with the specified status 
	  * id
	  */
	def withStatus(statusId: Int) = filter(model.statusId.column <=> statusId)
	
	/**
	  * @param statusIds Targeted status ids
	  * @return Copy of this access point that only includes response content types where status id is within 
	  * the specified value set
	  */
	def withStatuses(statusIds: IterableOnce[Int]) = filter(Condition.indexIn(model.statusId, statusIds))
}

