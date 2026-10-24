package vf.myapidoc.database.access.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.nosql.view.TimelineView
import vf.myapidoc.database.storable.api.ApiDbModel

/**
  * Common trait for access points which may be filtered based on api properties
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
trait FilterApis[+Repr] extends TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines api database properties
	  */
	def model = ApiDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes apis with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes apis where name is within the specified value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

