package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.DocUpdateFactory

import java.time.Instant

object DocUpdateData extends FromModelFactoryWithSchema[DocUpdateData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(
		PropertyDeclaration("apiId", IntType, Single("api_id")), 
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		DocUpdateData(valid("apiId").getInt, valid("created").getInstant)
}

/**
  * Represents an API documentation update
  * @param apiId   ID of the updated API
  * @param created Time when this doc update was added to the database
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class DocUpdateData(apiId: Int, created: Instant = Now) 
	extends DocUpdateFactory[DocUpdateData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("apiId" -> apiId, "created" -> created))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	
	override def withCreated(created: Instant) = copy(created = created)
}

