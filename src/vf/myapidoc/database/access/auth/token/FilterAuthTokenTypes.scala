package vf.myapidoc.database.access.auth.token

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel

/**
  * Common trait for access points which may be filtered based on auth token type properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterAuthTokenTypes[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines auth token type database properties
	  */
	def model = AuthTokenTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes auth token types with the specified api id
	  */
	def forApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes auth token types where api id is within the 
	  * specified value set
	  */
	def forApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
	
	/**
	  * @param nameNoSpaces name no spaces to target
	  * @return Copy of this access point that only includes auth token types with the specified name no 
	  * spaces
	  */
	def withNameMatching(nameNoSpaces: String) = filter(model.nameNoSpaces.column <=> nameNoSpaces)
	/**
	  * @param namesWithoutSpaces Targeted names without spaces
	  * @return Copy of this access point that only includes auth token types where name no spaces is within 
	  * the specified value set
	  */
	def withNameMatchingAnyOf(namesWithoutSpaces: Iterable[String]) = 
		filter(model.nameNoSpaces.column.in(namesWithoutSpaces))
}

