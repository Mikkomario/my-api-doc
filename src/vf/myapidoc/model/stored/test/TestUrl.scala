package vf.myapidoc.model.stored.test

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.test.url.AccessTestUrl
import vf.myapidoc.model.factory.test.TestUrlFactoryWrapper
import vf.myapidoc.model.partial.test.TestUrlData

object TestUrl extends StandardStoredFactory[TestUrlData, TestUrl]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = TestUrlData
}

/**
  * Represents a test url that has already been stored in the database. 
  * Documents a targeted API's root URL
  * @param id   ID of this test url in the database
  * @param data Wrapped test url data
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestUrl(id: Int, data: TestUrlData) 
	extends StoredModelConvertible[TestUrlData] with FromIdFactory[Int, TestUrl] 
		with TestUrlFactoryWrapper[TestUrlData, TestUrl]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this test url in the database
	  */
	def access = AccessTestUrl(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override def wrap(data: TestUrlData) = copy(data = data)
}

