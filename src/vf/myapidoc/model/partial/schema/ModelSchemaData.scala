package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.schema.ModelSchemaFactory
import vf.myapidoc.model.partial.api.{VersionedDocElementData, VersionedDocElementDataLike}

import java.time.Instant

object ModelSchemaData extends FromModelFactoryWithSchema[ModelSchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("name", StringType, isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ModelSchemaData(valid("originalUpdateId").getInt, valid("apiId").getInt, 
			valid("removingUpdateId").int, valid("name").getString, valid("description").getString)
}

/**
  * Defines properties in an input or output model / JSON object.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param apiId            ID of the API this model type belongs to.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param name             Name of this model type (/class). Empty if unnamed. 
  *                         Named model types are often referenced from multiple sources, while 
  *                         unnamed types are typically single-use.
  * @param description      Description of this model type's role / function.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ModelSchemaData(originalUpdateId: Int, apiId: Int, removingUpdateId: Option[Int] = None, name: String = "",
                           description: String = "")
	extends ModelSchemaFactory[ModelSchemaData] with VersionedDocElementData
		with VersionedDocElementDataLike[ModelSchemaData]
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this is a named model type
	  */
	def hasName = name.nonEmpty
	/**
	  * Whether this is a nameless model type
	  */
	def isNameless = !hasName
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("apiId" -> apiId, "name" -> name,
		"description" -> description))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withDescription(description: String) = copy(description = description)
	override def withName(name: String) = copy(name = name)
}

