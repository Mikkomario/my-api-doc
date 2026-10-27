package vf.myapidoc.model.partial.auth

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.factory.auth.AuthTokenTypeFactory
import vf.myapidoc.model.template.ClientModelConvertible

import java.time.Instant

object AuthTokenTypeData extends FromModelFactory[AuthTokenTypeData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("nameNoSpaces", StringType, Single("name_no_spaces")),
		PropertyDeclaration("useType", IntType, Single("use_type")),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: HasProperties) = schema.validate(model).flatMap { valid =>
		TokenUseType.fromValue(valid("useType")).map { useType =>
			AuthTokenTypeData(valid("apiId").getInt, valid("name").getString,
				valid("nameNoSpaces").getString, useType, valid("created").getInstant,
				valid("deprecatedAfter").instant)
		}
	}
}

/**
  * Represents some (custom) token type used by the documented API.
  * @param apiId           ID of the API where this token type is used.
  * @param name            (Human-readable) name of this token type
  * @param nameNoSpaces    A lowercase copy of this token's name with all whitespaces removed. 
  *                        Used for indexing and search.
  * @param useType         Determines how this token is applied.
  * @param created         Time when this auth token type was added to the database
  * @param deprecatedAfter Time when this auth token type became deprecated. None while this auth 
  *                        token type is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AuthTokenTypeData(apiId: Int, name: String, nameNoSpaces: String, useType: TokenUseType,
                             created: Instant = Now, deprecatedAfter: Option[Instant] = None)
	extends AuthTokenTypeFactory[AuthTokenTypeData] with ModelConvertible with ClientModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this auth token type has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this auth token type is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "name" -> name, "nameNoSpaces" -> nameNoSpaces,
		"useType" -> useType.id, "created" -> created, "deprecatedAfter" -> deprecatedAfter))
	override def toClientModel: Model = Model.from("name" -> name, "type" -> useType.toString.toLowerCase)
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withUseType(useType: TokenUseType) = copy(useType = useType)
}

