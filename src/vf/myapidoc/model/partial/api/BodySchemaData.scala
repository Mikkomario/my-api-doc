package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.{FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{BooleanType, InstantType, IntType}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.enumeration.PropertyType.AnyProp

import java.time.Instant

object BodySchemaData extends FromModelFactoryWithSchema[BodySchemaData] with FromModelFactory[BodySchemaData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("parentId", IntType, Single("parent_id")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("dataType", IntType, Single("data_type"), AnyProp.id),
		PropertyDeclaration("modelTypeId", IntType, Single("model_type_id"), isOptional = true),
		PropertyDeclaration("isArray", BooleanType, Single("is_array"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		BodySchemaData(valid("originalUpdateId").getInt, valid("parentId").getInt, 
			valid("removingUpdateId").int, PropertyType.fromValue(valid("dataType")), 
			valid("modelTypeId").int, valid("isArray").getBoolean)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new body schema
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param parentId         ID of the element that contains this body.
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @param dataType         Type of this body's contents.
	  * @param modelTypeId      If this body contains an object or objects, contains the ID of the 
	  *                         model schema that describes the applicable object structure.
	  * @param isArray          Whether this body contains an array of values. False if no more than 
	  *                         one value may be included.
	  * @return body schema with the specified properties
	  */
	def apply(originalUpdateId: Int, parentId: Int, removingUpdateId: Option[Int] = None,
	          dataType: PropertyType = AnyProp, modelTypeId: Option[Int] = None,
	          isArray: Boolean = false): BodySchemaData =
		_BodySchemaData(originalUpdateId, parentId, removingUpdateId, dataType, modelTypeId, isArray)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the body schema data trait
	  * @param originalUpdateId ID of the update that introduced this element (version)
	  * @param parentId         ID of the element that contains this body.
	  * @param removingUpdateId ID of the update that removed or replaced this element (version)
	  * @param dataType         Type of this body's contents.
	  * @param modelTypeId      If this body contains an object or objects, contains the ID of the 
	  *                         model schema that describes the applicable object structure.
	  * @param isArray          Whether this body contains an array of values. False if no more than 
	  *                         one value may be included.
	  * @author Mikko Hilpinen
	  * @since 27.10.2026
	  */
	private case class _BodySchemaData(originalUpdateId: Int, parentId: Int, removingUpdateId: Option[Int] = None,
	                                   dataType: PropertyType = AnyProp, modelTypeId: Option[Int] = None,
	                                   isArray: Boolean = false)
		extends BodySchemaData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param originalUpdateId ID of the update that introduced this element (version)
		  * @param parentId         ID of the element that contains this body.
		  * @param removingUpdateId ID of the update that removed or replaced this element (version)
		  * @param dataType         Type of this body's contents.
		  * @param modelTypeId      If this body contains an object or objects, contains the ID of the 
		  *                         model schema that describes the applicable object structure.
		  * @param isArray          Whether this body contains an array of values. False if no more than 
		  *                         one value may be included.
		  */
		override def copyBodySchema(originalUpdateId: Int, parentId: Int, removingUpdateId: Option[Int] = None,
		                            dataType: PropertyType = AnyProp, modelTypeId: Option[Int] = None,
		                            isArray: Boolean = false) =
			_BodySchemaData(originalUpdateId, parentId, removingUpdateId, dataType, modelTypeId, isArray)
	}
}

/**
  * Common trait for request & response body schemas.
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaData 
	extends BodySchemaDataLike[BodySchemaData] with VersionedDocElementData 
		with VersionedDocElementDataLike[BodySchemaData]

