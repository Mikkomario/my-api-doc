package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.TimelineView
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.DocUpdateDbModel

/**
  * Common trait for access points that may be filtered based on doc update properties
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait FilterDocUpdates[+Repr] extends TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines doc update database properties
	  */
	def model = DocUpdateDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes doc updates with the specified api id
	  */
	def ofApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes doc updates where api id is within the specified 
	  * value set
	  */
	def ofApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
}

