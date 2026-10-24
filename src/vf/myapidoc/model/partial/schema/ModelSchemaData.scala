package vf.myapidoc.model.partial.schema

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.schema.ModelSchemaFactory

import java.time.Instant

object ModelSchemaData extends FromModelFactoryWithSchema[ModelSchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("apiId", IntType, Single("api_id")),
		PropertyDeclaration("name", StringType, isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ModelSchemaData(valid("apiId").getInt, valid("name").getString, valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Defines properties in an input or output model / JSON object.
  * @param apiId           ID of the API this model type belongs to.
  * @param name            Name of this model type (/class). Empty if unnamed. 
  *                        Named model types are often referenced from multiple sources, while 
  *                        unnamed types are typically single-use.
  * @param description     Description of this model type's role / function.
  * @param created         Time when this model schema was added to the database
  * @param deprecatedAfter Time when this model schema became deprecated. None while this model 
  *                        schema is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ModelSchemaData(apiId: Int, name: String = "", description: String = "", created: Instant = Now,
                           deprecatedAfter: Option[Instant] = None)
	extends ModelSchemaFactory[ModelSchemaData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this model schema has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this model schema is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	/**
	 * @return Whether this is a named model type
	 */
	def hasName = name.nonEmpty
	/**
	 * @return Whether this is a nameless model type
	 */
	def isNameless = !hasName
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("apiId" -> apiId, "name" -> name, "description" -> description,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter))
	
	override def withApiId(apiId: Int) = copy(apiId = apiId)
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withName(name: String) = copy(name = name)
}

