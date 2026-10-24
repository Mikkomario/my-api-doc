package vf.myapidoc.model.partial.auth

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.auth.ScopeFactory

import java.time.Instant

object ScopeData extends FromModelFactoryWithSchema[ScopeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ScopeData(valid("apiId").getInt, valid("name").getString, valid("created").getInstant, 
			valid("deprecatedAfter").instant)
}

/**
  * Represents an authorization scope in the documented API.
  * @param apiId           ID of the API where this scope is used.
  * @param name            Name / identifier of this scope.
  * @param created         Time when this scope was added to the database
  * @param deprecatedAfter Time when this scope became deprecated. None while this scope is still 
  *                        valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ScopeData(apiId: Int, name: String, created: Instant = Now, deprecatedAfter: Option[Instant] = None)
	extends ScopeFactory[ScopeData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this scope has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this scope is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "name" -> name, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withName(name: String) = copy(name = name)
}

