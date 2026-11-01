package vf.myapidoc.database.store

import utopia.flow.util.StringExtensions._
import utopia.vault.database.{Connection, Store}
import vf.myapidoc.database.access.test.file.AccessTestsFile
import vf.myapidoc.database.access.test.url.AccessTestUrl
import vf.myapidoc.database.storable.test.{TestUrlDbModel, TestsFileDbModel}
import vf.myapidoc.model.partial.test.{TestUrlData, TestsFileData}

import java.nio.file.Path

/**
 * Provides functions for interacting with recorded tests
 * @author Mikko Hilpinen
 * @since 01.11.2026, v0.1
 */
object TestsDb
{
	// ATTRIBUTES   ------------------------
	
	/**
	 * An interface for storing test URLs.
	 * Receives 3 input parameters:
	 *      1. API ID
	 *      1. Api root node URL
	 *      1. URL name
	 *
	 * Assumes that URLs are matched using URL strings
	 */
	private val _storeUrls = Store(TestUrlDbModel)
		.apply { data: (Int, String, String) => TestUrlData(apiId = data._1, rootUrl = data._2, name = data._3) }
		// May change the URL name and API ID on update
		.updating { case ((apiId, _, name), existing, connection) =>
			implicit val c: Connection = connection
			val apiUpdated = {
				if (existing.apiId == apiId)
					None
				else {
					existing.access.apiId.set(apiId)
					Some(existing.withApiId(apiId))
				}
			}
			val nameUpdated = {
				if (name.isEmpty || existing.name == name)
					apiUpdated
				else {
					existing.access.name.set(name)
					Some(apiUpdated.getOrElse(existing).withName(name))
				}
			}
			nameUpdated
		}
	/**
	 * An interface for storing test file paths.
	 * Receives 2 input parameters:
	 *      1. API ID
	 *      1. Path to the tests-file
	 */
	private val _storePaths = Store(TestsFileDbModel) { data: (Int, Path) =>
		TestsFileData(apiId = data._1, path = data._2)
	}
	
	
	// OTHER    -------------------------
	
	/**
	 * Stores a test URL into the DB
	 * @param apiId ID of the targeted API
	 * @param rootNodeUrl URL to the APIs root node, including the protocol
	 * @param name API name. May be empty.
	 * @param connection Implicit DB connection
	 * @return Stored test URL entry
	 */
	def storeUrl(apiId: Int, rootNodeUrl: String, name: String = "")(implicit connection: Connection) = {
		val urlNoTrailingSlash = rootNodeUrl.notEndingWith("/")
		_storeUrls.single((apiId, urlNoTrailingSlash, name),
			existingMatch = AccessTestUrl.withRootUrl(urlNoTrailingSlash).pull)
	}
	/**
	 * Stores a tests file path to the DB
	 * @param apiId ID of the targeted API
	 * @param path Path to the tests (Markdown) file
	 * @param connection Implicit DB connection
	 * @return Stored tests file entry
	 */
	def storeTestPath(apiId: Int, path: Path)(implicit connection: Connection) =
		_storePaths.single(apiId -> path, existingMatch = AccessTestsFile.forApi(apiId).atPath(path).pull)
}
