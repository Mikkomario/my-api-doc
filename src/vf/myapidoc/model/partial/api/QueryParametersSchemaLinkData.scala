package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import vf.myapidoc.model.factory.api.QueryParametersSchemaLinkFactory

object QueryParametersSchemaLinkData extends FromModelFactoryWithSchema[QueryParametersSchemaLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("implementationId", IntType, Single("implementation_id")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		QueryParametersSchemaLinkData(valid("originalUpdateId").getInt, valid("implementationId").getInt, 
			valid("modelTypeId").getInt, valid("removingUpdateId").int)
}

/**
  * Applies a specific schema to the query parameters accepted by an API node.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param implementationId ID of the method implementation that receives the linked parameters.
  * @param modelTypeId      ID of the schema that defines the accepted path parameters.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class QueryParametersSchemaLinkData(originalUpdateId: Int, implementationId: Int, modelTypeId: Int,
                                         removingUpdateId: Option[Int] = None)
	extends QueryParametersSchemaLinkFactory[QueryParametersSchemaLinkData] with VersionedDocElementData 
		with VersionedDocElementDataLike[QueryParametersSchemaLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Pair("implementationId" -> implementationId,
		"modelTypeId" -> modelTypeId))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withImplementationId(implementationId: Int) = copy(implementationId = implementationId)
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
}

