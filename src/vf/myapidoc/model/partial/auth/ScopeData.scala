package vf.myapidoc.model.partial.auth

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.auth.ScopeFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}

import java.time.Instant

object ScopeData extends FromModelFactoryWithSchema[ScopeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ScopeData(valid("originalUpdateId").getInt, valid("apiId").getInt, valid("name").getString, 
			valid("removingUpdateId").int)
}

/**
  * Represents an authorization scope in the documented API.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param apiId            ID of the API where this scope is used.
  * @param name             Name / identifier of this scope.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ScopeData(originalUpdateId: Int, apiId: Int, name: String, removingUpdateId: Option[Int] = None) 
	extends ScopeFactory[ScopeData] with VersionedDocElementData with VersionedDocElementDataLike[ScopeData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Pair("apiId" -> apiId, 
		"name" -> name))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withName(name: String) = copy(name = name)
}

