package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.api.ApiNodeGroupFactory

import java.time.Instant

object ApiNodeGroupData extends FromModelFactoryWithSchema[ApiNodeGroupData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeGroupData(valid("originalUpdateId").getInt, valid("apiId").getInt, valid("name").getString, 
			valid("removingUpdateId").int, valid("description").getString)
}

/**
  * Defines a group of API nodes
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param apiId            ID of the API this node group belongs to
  * @param name             (Human readable) name of this node group
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param description      Description of this group's function or theme
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeGroupData(originalUpdateId: Int, apiId: Int, name: String, removingUpdateId: Option[Int] = None,
                            description: String = "")
	extends ApiNodeGroupFactory[ApiNodeGroupData] with VersionedDocElementData 
		with VersionedDocElementDataLike[ApiNodeGroupData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("apiId" -> apiId, "name" -> name,
		"description" -> description))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withDescription(description: String) = copy(description = description)
	override def withName(name: String) = copy(name = name)
}

