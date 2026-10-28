package vf.myapidoc.model.partial.auth

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.{FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.enumeration.TokenUseType.Bearer
import vf.myapidoc.model.factory.auth.AuthTokenTypeFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}
import vf.myapidoc.model.template.ClientModelConvertible

import java.time.Instant

object AuthTokenTypeData 
	extends FromModelFactoryWithSchema[AuthTokenTypeData] with FromModelFactory[AuthTokenTypeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("nameNoSpaces", StringType, Single("name_no_spaces")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("useType", IntType, Single("use_type"), Bearer.id)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AuthTokenTypeData(valid("originalUpdateId").getInt, valid("apiId").getInt, valid("name").getString, 
			valid("nameNoSpaces").getString, valid("removingUpdateId").int, 
			TokenUseType.fromValue(valid("useType")))
}

/**
  * Represents some (custom) token type used by the documented API.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param apiId            ID of the API where this token type is used.
  * @param name             (Human-readable) name of this token type
  * @param nameNoSpaces     A lowercase copy of this token's name with all whitespaces removed. 
  *                         Used for indexing and search.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param useType          Determines how this token is applied.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AuthTokenTypeData(originalUpdateId: Int, apiId: Int, name: String, nameNoSpaces: String,
                             removingUpdateId: Option[Int] = None, useType: TokenUseType = Bearer)
	extends AuthTokenTypeFactory[AuthTokenTypeData] with VersionedDocElementData 
		with VersionedDocElementDataLike[AuthTokenTypeData] with ModelConvertible with ClientModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toClientModel: Model = Model.from("name" -> name, "type" -> useType.toString.toLowerCase)
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("apiId" -> apiId, "name" -> name,
		"nameNoSpaces" -> nameNoSpaces, "useType" -> useType.id))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withUseType(useType: TokenUseType) = copy(useType = useType)
}

