package vf.myapidoc.model.factory.schema

import utopia.flow.util.UncertainBoolean
import vf.myapidoc.model.enumeration.PropertyType

import java.time.Instant

/**
  * Common trait for property schema-related factories which allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PropertySchemaFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param contentTypeId New content type id to assign
	  * @return Copy of this item with the specified content type id
	  */
	def withContentTypeId(contentTypeId: Int): A
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param dataType New data type to assign
	  * @return Copy of this item with the specified data type
	  */
	def withDataType(dataType: PropertyType): A
	
	/**
	  * @param deprecatedAfter New deprecated after to assign
	  * @return Copy of this item with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant): A
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	
	/**
	  * @param isArray New is array to assign
	  * @return Copy of this item with the specified is array
	  */
	def withIsArray(isArray: UncertainBoolean): A
	
	/**
	  * @param isOptional New is optional to assign
	  * @return Copy of this item with the specified is optional
	  */
	def withIsOptional(isOptional: Boolean): A
	
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

