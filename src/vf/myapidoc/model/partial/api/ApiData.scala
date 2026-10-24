package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.ApiFactory

import java.time.Instant

object ApiData extends FromModelFactoryWithSchema[ApiData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(PropertyDeclaration("name", StringType), PropertyDeclaration("created", 
			InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiData(valid("name").getString, valid("created").getInstant)
}

/**
  * Represents a documented API
  * @param name    (Human readable) name of this API
  * @param created Time when this api was added to the database
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class ApiData(name: String, created: Instant = Now) extends ApiFactory[ApiData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("name" -> name, "created" -> created))
	
	override def withCreated(created: Instant) = copy(created = created)
	
	override def withName(name: String) = copy(name = name)
}

