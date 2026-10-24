package vf.myapidoc.database.access.api.node.auth.scope

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.node.auth.FilterNodeAuthLinks
import vf.myapidoc.database.storable.api.NodeAuthScopeLinkDbModel

/**
  * Common trait for access points which may be filtered based on node auth scope link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterNodeAuthScopeLinks[+Repr] extends FilterNodeAuthLinks[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines node auth scope link database properties
	  */
	def model = NodeAuthScopeLinkDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param scopeId scope id to target
	  * @return Copy of this access point that only includes node auth scope links with the specified scope id
	  */
	def requiringScope(scopeId: Int) = filter(model.scopeId.column <=> scopeId)
	
	/**
	  * @param scopeIds Targeted scope ids
	  * @return Copy of this access point that only includes node auth scope links where scope id is within 
	  * the specified value set
	  */
	def requiringScopes(scopeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.scopeId, scopeIds))
}

