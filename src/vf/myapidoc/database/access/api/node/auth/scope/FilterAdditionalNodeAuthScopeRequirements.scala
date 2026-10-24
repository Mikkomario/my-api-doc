package vf.myapidoc.database.access.api.node.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.AdditionalNodeAuthScopeRequirementDbModel

/**
  * Common trait for access points which may be filtered based on additional node auth scope 
  * requirement properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterAdditionalNodeAuthScopeRequirements[+Repr] extends Filterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines additional node auth scope requirement database properties
	  */
	def model = AdditionalNodeAuthScopeRequirementDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param scopeLinkId scope link id to target
	  * @return Copy of this access point that only includes additional node auth scope requirements with the 
	  * specified scope link id
	  */
	def extendingLink(scopeLinkId: Int) = filter(model.scopeLinkId.column <=> scopeLinkId)
	
	/**
	  * @param scopeLinkIds Targeted scope link ids
	  * @return Copy of this access point that only includes additional node auth scope requirements where 
	  * scope link id is within the specified value set
	  */
	def extendingLinks(scopeLinkIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.scopeLinkId, scopeLinkIds))
	
	/**
	  * @param scopeId scope id to target
	  * @return Copy of this access point that only includes additional node auth scope requirements with the 
	  * specified scope id
	  */
	def requiringScope(scopeId: Int) = filter(model.scopeId.column <=> scopeId)
	
	/**
	  * @param scopeIds Targeted scope ids
	  * @return Copy of this access point that only includes additional node auth scope requirements where 
	  * scope id is within the specified value set
	  */
	def requiringScopes(scopeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.scopeId, scopeIds))
}

