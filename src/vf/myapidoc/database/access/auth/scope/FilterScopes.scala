package vf.myapidoc.database.access.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.auth.ScopeDbModel

/**
  * Common trait for access points which may be filtered based on scope properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterScopes[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines scope database properties
	  */
	def model = ScopeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes scopes with the specified api id
	  */
	def forApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes scopes where api id is within the specified 
	  * value set
	  */
	def forApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes scopes with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes scopes where name is within the specified value 
	  * set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

