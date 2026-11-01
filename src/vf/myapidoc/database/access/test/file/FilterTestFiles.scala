package vf.myapidoc.database.access.test.file

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.test.TestsFileDbModel

/**
  * Common trait for access points that may be filtered based on tests file properties
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
trait FilterTestFiles[+Repr] extends Filterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines tests file database properties
	  */
	def model = TestsFileDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param apiId api id to target
	  * @return Copy of this access point that only includes test files with the specified api id
	  */
	def forApi(apiId: Int) = filter(model.apiId.column <=> apiId)
	
	/**
	  * @param apiIds Targeted api ids
	  * @return Copy of this access point that only includes test files where api id is within the specified 
	  * value set
	  */
	def forApis(apiIds: IterableOnce[Int]) = filter(Condition.indexIn(model.apiId, apiIds))
}

