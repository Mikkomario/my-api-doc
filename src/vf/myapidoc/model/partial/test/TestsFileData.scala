package vf.myapidoc.model.partial.test

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.Now
import vf.myapidoc.model.factory.test.TestsFileFactory

import java.nio.file.Path
import java.time.Instant

object TestsFileData extends FromModelFactoryWithSchema[TestsFileData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")), 
		PropertyDeclaration("path", StringType), 
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model): TestsFileData =
		TestsFileData(apiId = model("apiId").getInt, path = model("path").getString: Path,
			created = model("created").getInstant)
}

/**
  * Documents the location of a single tests-file
  * @param apiId   ID of the tested API
  * @param path    Path to the tests-file
  * @param created Time when this tests file was added to the database
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestsFileData(apiId: Int, path: Path, created: Instant = Now) 
	extends TestsFileFactory[TestsFileData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "path" -> path.toJson, "created" -> created))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withPath(path: Path) = copy(path = path)
}

