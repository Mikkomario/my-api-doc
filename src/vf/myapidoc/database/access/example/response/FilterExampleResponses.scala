package vf.myapidoc.database.access.example.response

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.TimelineView
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.example.ExampleResponseDbModel

/**
  * Common trait for access points that may be filtered based on example response properties
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait FilterExampleResponses[+Repr] extends TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines example response database properties
	  */
	def model = ExampleResponseDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param testedApiVersionId tested api version id to target
	  * @return Copy of this access point that only includes example responses with the specified tested api 
	  * version id
	  */
	def fromApiVersion(testedApiVersionId: Int) = 
		filter(model.testedApiVersionId.column <=> testedApiVersionId)
	
	/**
	  * @param testedApiVersionIds Targeted tested api version ids
	  * @return Copy of this access point that only includes example responses where tested api version id is 
	  * within the specified value set
	  */
	def fromApiVersions(testedApiVersionIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.testedApiVersionId, testedApiVersionIds))
	
	/**
	  * @param statusId status id to target
	  * @return Copy of this access point that only includes example responses with the specified status id
	  */
	def matchingStatus(statusId: Int) = filter(model.statusId.column <=> statusId)
	
	/**
	  * @param statusIds Targeted status ids
	  * @return Copy of this access point that only includes example responses where status id is within the 
	  * specified value set
	  */
	def matchingStatuses(statusIds: IterableOnce[Int]) = filter(Condition.indexIn(model.statusId, statusIds))
}

