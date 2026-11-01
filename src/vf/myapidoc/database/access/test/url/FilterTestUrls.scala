package vf.myapidoc.database.access.test.url

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.test.TestUrlDbModel

/**
  * Common trait for access points that may be filtered based on test url properties
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
trait FilterTestUrls[+Repr] extends Filterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines test url database properties
	  */
	def model = TestUrlDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes test urls with the specified api id
	  */
	def ofApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes test urls where api id is within the specified 
	  * value set
	  */
	def ofApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
}

