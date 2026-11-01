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
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes test urls with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes test urls where name is within the specified 
	  * value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
	
	/**
	  * @param rootUrl root url to target
	  * @return Copy of this access point that only includes test urls with the specified root url
	  */
	def withRootUrl(rootUrl: String) = filter(model.rootUrl.column <=> rootUrl)
	/**
	  * @param rootUrls Targeted root urls
	  * @return Copy of this access point that only includes test urls where root url is within the specified 
	  * value set
	  */
	def withRootUrls(rootUrls: Iterable[String]) = filter(model.rootUrl.column.in(rootUrls))
	
	/**
	 * @param string A string
	 * @return Access to test URLS that contain the specified string either in the URL or in their name
	 */
	def containing(string: String) =
		filter(model.name.column.contains(string) || model.rootUrl.column.contains(string))
}

