package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.PathParametersSchemaLinkFactory

import java.time.Instant

object PathParametersSchemaLinkData extends FromModelFactoryWithSchema[PathParametersSchemaLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("nodeId", IntType, Single("node_id")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id")),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PathParametersSchemaLinkData(valid("nodeId").getInt, valid("modelTypeId").getInt, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Applies a specific schema to the path parameters received by an API node.
  * @param nodeId          ID of the API node that accepts these parameters.
  * @param modelTypeId     ID of the model type that describes the received path parameters.
  * @param created         Time when this path parameters schema link was added to the database
  * @param deprecatedAfter Time when this path parameters schema link became deprecated. None 
  *                        while this path parameters schema link is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PathParametersSchemaLinkData(nodeId: Int, modelTypeId: Int, created: Instant = Now,
                                        deprecatedAfter: Option[Instant] = None)
	extends PathParametersSchemaLinkFactory[PathParametersSchemaLinkData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this path parameters schema link has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this path parameters schema link is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("nodeId" -> nodeId, "modelTypeId" -> modelTypeId, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter))
	
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = modelTypeId)
	override def withNodeId(nodeId: Int) = copy(nodeId = nodeId)
}

