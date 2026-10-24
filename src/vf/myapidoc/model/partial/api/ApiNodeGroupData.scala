package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.ApiNodeGroupFactory

import java.time.Instant

object ApiNodeGroupData extends FromModelFactoryWithSchema[ApiNodeGroupData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeGroupData(valid("apiId").getInt, valid("name").getString, valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Defines a group of API nodes
  * @param apiId           ID of the API this node group belongs to
  * @param name            (Human readable) name of this node group
  * @param description     Description of this group's function or theme
  * @param created         Time when this api node group was added to the database
  * @param deprecatedAfter Time when this api node group became deprecated. None while this api 
  *                        node group is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeGroupData(apiId: Int, name: String, description: String = "", created: Instant = Now,
                            deprecatedAfter: Option[Instant] = None)
	extends ApiNodeGroupFactory[ApiNodeGroupData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this api node group has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this api node group is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "name" -> name, "description" -> description,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withName(name: String) = copy(name = name)
}

