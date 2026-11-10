package vf.myapidoc.model.stored.schema

import utopia.vault.store.StandardStoredFactory
import vf.myapidoc.model.partial.schema.PropertySchemaData

object PropertySchema extends StandardStoredFactory[PropertySchemaData, PropertySchema]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = PropertySchemaData
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new property schema
	  * @param id   ID of this property schema in the database
	  * @param data Wrapped property schema data
	  * @return property schema with the specified id and wrapped data
	  */
	def apply(id: Int, data: PropertySchemaData): PropertySchema = _StoredPropertySchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the property schema trait
	  * @param id   ID of this property schema in the database
	  * @param data Wrapped property schema data
	  * @author Mikko Hilpinen
	  * @since 15.10.2026
	  */
	private case class _StoredPropertySchema(id: Int, data: PropertySchemaData) extends PropertySchema
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		override def wrap(data: PropertySchemaData) = copy(data = data)
	}
}

/**
  * Represents a property schema that has already been stored in the database. 
  * Defines an individual property in a model schema.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PropertySchema extends StoredPropertySchemaLike[PropertySchema]
