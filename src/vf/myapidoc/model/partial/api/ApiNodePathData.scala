package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import vf.myapidoc.model.factory.api.ApiNodePathFactory

object ApiNodePathData extends FromModelFactoryWithSchema[ApiNodePathData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")), 
		PropertyDeclaration("nodeId", IntType, Single("node_id")), 
		PropertyDeclaration("relative", StringType), 
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true), 
		PropertyDeclaration("paramsModelSchemaId", IntType, Single("params_model_schema_id"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model) = 
		ApiNodePathData(originalUpdateId = model("originalUpdateId").getInt, nodeId = model("nodeId").getInt, 
			relative = model("relative").getString, removingUpdateId = model("removingUpdateId").int, 
			paramsModelSchemaId = model("paramsModelSchemaId").int)
}

/**
  * Represents a path leading to an API node. Relative to the parent node (if specified) or the 
  * API root.
  * @param originalUpdateId    ID of the update that introduced this element (version)
  * @param nodeId              ID of the API node at the end of this path
  * @param relative            This path as a string; Relative to the parent node (if defined) or 
  *                            the API root. 
  *                            May refer to path parameters.
  * @param removingUpdateId    ID of the update that removed or replaced this element (version)
  * @param paramsModelSchemaId ID of the model type that describes the received path parameters. 
  *                            None if no path parameters are accepted.
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class ApiNodePathData(originalUpdateId: Int, nodeId: Int, relative: String, removingUpdateId: Option[Int] = None,
                           paramsModelSchemaId: Option[Int] = None)
	extends ApiNodePathFactory[ApiNodePathData] with VersionedDocElementData 
		with VersionedDocElementDataLike[ApiNodePathData]
{
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++
		Model(Vector("nodeId" -> nodeId, "relative" -> relative, "paramsModelSchemaId" -> paramsModelSchemaId))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withNodeId(nodeId: Int) = copy(nodeId = nodeId)
	override def withParamsModelSchemaId(paramsModelSchemaId: Int) = 
		copy(paramsModelSchemaId = Some(paramsModelSchemaId))
	override def withRelative(relative: String) = copy(relative = relative)
}

