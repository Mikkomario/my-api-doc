package vf.myapidoc.model.partial.test

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.test.TestUrlFactory

import java.time.Instant

object TestUrlData extends FromModelFactoryWithSchema[TestUrlData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")), 
		PropertyDeclaration("rootUrl", StringType, Single("root_url")), 
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model): TestUrlData =
		TestUrlData(apiId = model("apiId").getInt, rootUrl = model("rootUrl").getString,
			created = model("created").getInstant)
}

/**
  * Documents a targeted API's root URL
  * @param apiId   ID of the targeted API
  * @param rootUrl URL to the root API node. E.g. "https://domain/api/v1"
  * @param created Time when this test url was added to the database
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class TestUrlData(apiId: Int, rootUrl: String, created: Instant = Now) 
	extends TestUrlFactory[TestUrlData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "rootUrl" -> rootUrl, "created" -> created))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withRootUrl(rootUrl: String) = copy(rootUrl = rootUrl)
}

