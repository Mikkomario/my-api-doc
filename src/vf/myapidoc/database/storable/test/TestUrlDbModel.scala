package vf.myapidoc.database.storable.test

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.test.TestUrlFactory
import vf.myapidoc.model.partial.test.TestUrlData
import vf.myapidoc.model.stored.test.TestUrl

import java.time.Instant

/**
  * Used for constructing TestUrlDbModel instances and for inserting test urls to the database
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
object TestUrlDbModel 
	extends StorableFactory[TestUrlDbModel, TestUrl, TestUrlData] with FromIdFactory[Int, TestUrlDbModel] 
		with HasIdProperty with TestUrlFactory[TestUrlDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	
	/**
	  * Database property used for interacting with root urls
	  */
	lazy val rootUrl = property("rootUrl")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.testUrl
	
	override def apply(data: TestUrlData): TestUrlDbModel = 
		apply(None, Some(data.apiId), data.rootUrl, Some(data.created))
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withRootUrl(rootUrl: String) = apply(rootUrl = rootUrl)
	
	override protected def complete(id: Value, data: TestUrlData) = TestUrl(id.getInt, data)
}

/**
  * Used for interacting with TestUrls in the database
  * @param id test url database id
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestUrlDbModel(id: Option[Int] = None, apiId: Option[Int] = None, rootUrl: String = "", 
	created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, TestUrlDbModel] 
		with TestUrlFactory[TestUrlDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(TestUrlDbModel.id.name -> id, TestUrlDbModel.apiId.name -> apiId, 
			TestUrlDbModel.rootUrl.name -> rootUrl, TestUrlDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = TestUrlDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withRootUrl(rootUrl: String) = copy(rootUrl = rootUrl)
}

