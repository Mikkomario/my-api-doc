package vf.myapidoc.database.access.api.response.status

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.PossibleResponseStatusDbModel

/**
  * Common trait for access points which may be filtered based on possible response status 
  * properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterPossibleResponseStatuses[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines possible response status database properties
	  */
	def model = PossibleResponseStatusDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param implementationId implementation id to target
	  * @return Copy of this access point that only includes possible response statuses with the specified 
	  * implementation id
	  */
	def fromImplementation(implementationId: Int) = filter(model.implementationId.column <=> implementationId)
	
	/**
	  * @param implementationIds Targeted implementation ids
	  * @return Copy of this access point that only includes possible response statuses where implementation 
	  * id is within the specified value set
	  */
	def fromImplementations(implementationIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.implementationId, implementationIds))
	
	/**
	  * @param statusCode status code to target
	  * @return Copy of this access point that only includes possible response statuses with the specified 
	  * status code
	  */
	def withCode(statusCode: Int) = filter(model.statusCode.column <=> statusCode)
	
	/**
	  * @param statusCodes Targeted status codes
	  * @return Copy of this access point that only includes possible response statuses where status code is 
	  * within the specified value set
	  */
	def withCodes(statusCodes: IterableOnce[Int]) = filter(Condition.indexIn(model.statusCode, statusCodes))
}

