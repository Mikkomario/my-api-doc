package vf.myapidoc.model.partial.example

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.Now
import vf.myapidoc.model.factory.example.ExampleResponseFactory

import java.nio.file.Path
import java.time.Instant

object ExampleResponseData extends FromModelFactoryWithSchema[ExampleResponseData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("testedApiVersionId", IntType, Single("tested_api_version_id")), 
		PropertyDeclaration("statusId", IntType, Single("status_id")), 
		PropertyDeclaration("relativeFilePath", StringType, Single("relative_file_path")), 
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ExampleResponseData(valid("testedApiVersionId").getInt, valid("statusId").getInt, 
			valid("relativeFilePath").getString: Path, valid("created").getInstant)
}

/**
  * Represents a response received from a tested API
  * @param testedApiVersionId ID of the tested API (document) version
  * @param statusId           ID of the response (status) documentation entry that matches the 
  *                           received response
  * @param relativeFilePath   Path to the file where the response body is stored. Relative to the 
  *                           examples directory.
  * @param created            Time when this example response was added to the database
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
case class ExampleResponseData(testedApiVersionId: Int, statusId: Int, relativeFilePath: Path, created: Instant = Now)
	extends ExampleResponseFactory[ExampleResponseData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("testedApiVersionId" -> testedApiVersionId, "statusId" -> statusId,
		"relativeFilePath" -> relativeFilePath.toJson, "created" -> created))
	
	override def withCreated(created: Instant) = copy(created = created)
	override def withRelativeFilePath(relativeFilePath: Path) = copy(relativeFilePath = relativeFilePath)
	override def withStatusId(statusId: Int) = copy(statusId = statusId)
	override def withTestedApiVersionId(testedApiVersionId: Int) =
		copy(testedApiVersionId = testedApiVersionId)
}

