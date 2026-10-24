package vf.myapidoc.database.access.schema.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel

/**
  * Common trait for access points which may be filtered based on model schema properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterModelSchemas[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines model schema database properties
	  */
	def model = ModelSchemaDbModel
	
	/**
	 * @return Access limited to named models
	 */
	def named = filter(model.name.isNotNull)
	/**
	 * @return Access limited to nameless models
	 */
	def nameless = filter(model.name.isNull)
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes model schemas with the specified api id
	  */
	def inApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes model schemas where api id is within the 
	  * specified value set
	  */
	def inApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes model schemas with the specified name
	  */
	def named(name: String) = filter(model.name.column <=> name)
	
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes model schemas where name is within the specified 
	  * value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

