package vf.myapidoc.database.storable.example

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.example.ExampleResponseFactory
import vf.myapidoc.model.partial.example.ExampleResponseData
import vf.myapidoc.model.stored.example.ExampleResponse

import java.nio.file.Path
import java.time.Instant

/**
  * Used for constructing ExampleResponseDbModel instances and for inserting example responses to 
  * the database
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
object ExampleResponseDbModel 
	extends StorableFactory[ExampleResponseDbModel, ExampleResponse, ExampleResponseData] 
		with FromIdFactory[Int, ExampleResponseDbModel] with HasIdProperty 
		with ExampleResponseFactory[ExampleResponseDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with tested api version ids
	  */
	lazy val testedApiVersionId = property("testedApiVersionId")
	
	/**
	  * Database property used for interacting with status ids
	  */
	lazy val statusId = property("statusId")
	
	/**
	  * Database property used for interacting with relative file paths
	  */
	lazy val relativeFilePath = property("relativeFilePath")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.exampleResponse
	
	override def apply(data: ExampleResponseData): ExampleResponseDbModel = 
		apply(None, Some(data.testedApiVersionId), Some(data.statusId), data.relativeFilePath.toJson, 
			Some(data.created))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withRelativeFilePath(relativeFilePath: Path) = apply(relativeFilePath = 
		relativeFilePath.toJson)
	
	override def withStatusId(statusId: Int) = apply(statusId = Some(statusId))
	
	override def withTestedApiVersionId(testedApiVersionId: Int) = 
		apply(testedApiVersionId = Some(testedApiVersionId))
	
	override protected def complete(id: Value, data: ExampleResponseData) = ExampleResponse(id.getInt, data)
}

/**
  * Used for interacting with ExampleResponses in the database
  * @param id example response database id
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExampleResponseDbModel(id: Option[Int] = None, testedApiVersionId: Option[Int] = None, 
	statusId: Option[Int] = None, relativeFilePath: String = "", created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ExampleResponseDbModel] 
		with ExampleResponseFactory[ExampleResponseDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ExampleResponseDbModel.id.name -> id, 
			ExampleResponseDbModel.testedApiVersionId.name -> testedApiVersionId, 
			ExampleResponseDbModel.statusId.name -> statusId, 
			ExampleResponseDbModel.relativeFilePath.name -> relativeFilePath, 
			ExampleResponseDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ExampleResponseDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withRelativeFilePath(relativeFilePath: Path) = copy(relativeFilePath = 
		relativeFilePath.toJson)
	
	override def withStatusId(statusId: Int) = copy(statusId = Some(statusId))
	
	override def withTestedApiVersionId(testedApiVersionId: Int) = 
		copy(testedApiVersionId = Some(testedApiVersionId))
}

