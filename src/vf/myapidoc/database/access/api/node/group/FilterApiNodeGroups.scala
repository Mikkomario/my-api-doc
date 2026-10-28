package vf.myapidoc.database.access.api.node.group

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel

/**
  * Common trait for access points that may be filtered based on api node group properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterApiNodeGroups[+Repr] extends FilterVersionedDocElements[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines api node group database properties
	  */
	def model = ApiNodeGroupDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes api node groups with the specified api id
	  */
	def inApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes api node groups where api id is within the 
	  * specified value set
	  */
	def inApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes api node groups with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes api node groups where name is within the 
	  * specified value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

