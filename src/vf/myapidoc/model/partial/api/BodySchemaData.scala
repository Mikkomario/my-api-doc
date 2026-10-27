package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.time.Now
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

object BodySchemaData extends FromModelFactory[BodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("parentId", IntType, Single("parent_id")),
		PropertyDeclaration("dataType", IntType, Single("data_type")),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true),
		PropertyDeclaration("created", InstantType, isOptional = true),
		PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: HasProperties) = schema.validate(model).map { valid =>
		BodySchemaData(valid("parentId").getInt, PropertyType.fromValue(valid("dataType")), valid("modelTypeId").int,
			valid("created").getInstant, valid("deprecatedAfter").instant, valid("isArray").getBoolean)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new body schema
	  * @param parentId        ID of the element that contains this body.
	  * @param dataType        Type of this body's contents.
	  * @param modelTypeId     If this body contains an object or objects, contains the ID of the 
	  *                        model schema that describes the applicable object structure.
	  * @param created         Time when this body schema was added to the database
	  * @param deprecatedAfter Time when this body schema became deprecated. None while this body 
	  *                        schema is still valid.
	  * @param isArray         Whether this body contains an array of values. False if no more than 
	  *                        one value may be included.
	  * @return body schema with the specified properties
	  */
	def apply(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int] = None, created: Instant = Now,
	          deprecatedAfter: Option[Instant] = None, isArray: Boolean = false): BodySchemaData =
		_BodySchemaData(parentId, dataType, modelTypeId, created, deprecatedAfter, isArray)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the body schema data trait
	  * @param parentId        ID of the element that contains this body.
	  * @param dataType        Type of this body's contents.
	  * @param modelTypeId     If this body contains an object or objects, contains the ID of the 
	  *                        model schema that describes the applicable object structure.
	  * @param created         Time when this body schema was added to the database
	  * @param deprecatedAfter Time when this body schema became deprecated. None while this body 
	  *                        schema is still valid.
	  * @param isArray         Whether this body contains an array of values. False if no more than 
	  *                        one value may be included.
	  * @author Mikko Hilpinen
	  * @since 27.10.2026
	  */
	private case class _BodySchemaData(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int] = None,
	                                   created: Instant = Now, deprecatedAfter: Option[Instant] = None,
	                                   isArray: Boolean = false)
		extends BodySchemaData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param parentId        ID of the element that contains this body.
		  * @param dataType        Type of this body's contents.
		  * @param modelTypeId     If this body contains an object or objects, contains the ID of the 
		  *                        model schema that describes the applicable object structure.
		  * @param created         Time when this body schema was added to the database
		  * @param deprecatedAfter Time when this body schema became deprecated. None while this body 
		  *                        schema is still valid.
		  * @param isArray         Whether this body contains an array of values. False if no more than 
		  *                        one value may be included.
		  */
		override def copyBodySchema(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int] = None, 
			created: Instant = Now, deprecatedAfter: Option[Instant] = None, isArray: Boolean = false) = 
			_BodySchemaData(parentId, dataType, modelTypeId, created, deprecatedAfter, isArray)
	}
}

/**
  * Common trait for request & response body schemas.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaData extends BodySchemaDataLike[BodySchemaData]
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this body schema has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this body schema is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
}

