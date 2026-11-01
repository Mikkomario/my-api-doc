package vf.myapidoc.database.storable.test

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.test.TestsFileFactory
import vf.myapidoc.model.partial.test.TestsFileData
import vf.myapidoc.model.stored.test.TestsFile

import java.nio.file.Path
import java.time.Instant

/**
  * Used for constructing TestsFileDbModel instances and for inserting test files to the database
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
object TestsFileDbModel 
	extends StorableFactory[TestsFileDbModel, TestsFile, TestsFileData] 
		with FromIdFactory[Int, TestsFileDbModel] with HasIdProperty with TestsFileFactory[TestsFileDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	
	/**
	  * Database property used for interacting with paths
	  */
	lazy val path = property("path")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.testsFile
	
	override def apply(data: TestsFileData): TestsFileDbModel = 
		apply(None, Some(data.apiId), data.path.toJson, Some(data.created))
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withPath(path: Path) = apply(path = path.toJson)
	
	override protected def complete(id: Value, data: TestsFileData) = TestsFile(id.getInt, data)
}

/**
  * Used for interacting with TestFiles in the database
  * @param id tests file database id
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestsFileDbModel(id: Option[Int] = None, apiId: Option[Int] = None, path: String = "", 
	created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, TestsFileDbModel] 
		with TestsFileFactory[TestsFileDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(TestsFileDbModel.id.name -> id, TestsFileDbModel.apiId.name -> apiId, 
			TestsFileDbModel.path.name -> path, TestsFileDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = TestsFileDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withPath(path: Path) = copy(path = path.toJson)
}

