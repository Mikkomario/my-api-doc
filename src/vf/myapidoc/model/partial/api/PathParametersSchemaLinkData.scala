package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.api.PathParametersSchemaLinkFactory

import java.time.Instant

object PathParametersSchemaLinkData extends FromModelFactoryWithSchema[PathParametersSchemaLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PathParametersSchemaLinkData(valid("originalUpdateId").getInt, valid("nodeId").getInt, 
			valid("modelTypeId").getInt, valid("removingUpdateId").int)
}

/**
  * Applies a specific schema to the path parameters received by an API node.
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param nodeId           ID of the API node that accepts these parameters.
  * @param modelTypeId      ID of the model type that describes the received path parameters.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PathParametersSchemaLinkData(originalUpdateId: Int, nodeId: Int, modelTypeId: Int,
                                        removingUpdateId: Option[Int] = None)
	extends PathParametersSchemaLinkFactory[PathParametersSchemaLinkData] with VersionedDocElementData 
		with VersionedDocElementDataLike[PathParametersSchemaLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		super[VersionedDocElementData].toModel ++ Model(Pair("nodeId" -> nodeId, 
			"modelTypeId" -> modelTypeId))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
	override def withNodeId(nodeId: Int) = copy(nodeId = nodeId)
}

