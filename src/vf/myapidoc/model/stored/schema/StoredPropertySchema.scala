package vf.myapidoc.model.stored.schema

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.schema.prop.AccessPropertySchema
import vf.myapidoc.model.factory.schema.PropertySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike

object StoredPropertySchema extends StandardStoredFactory[PropertySchemaData, StoredPropertySchema]
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
	def apply(id: Int, data: PropertySchemaData): StoredPropertySchema = _StoredPropertySchema(id, data)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the property schema trait
	  * @param id   ID of this property schema in the database
	  * @param data Wrapped property schema data
	  * @author Mikko Hilpinen
	  * @since 15.10.2026
	  */
	private case class _StoredPropertySchema(id: Int, data: PropertySchemaData) extends StoredPropertySchema
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrappedFactory = data
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(data: PropertySchemaData) = copy(data = data)
	}
}

/**
  * Represents a property schema that has already been stored in the database. 
  * Defines an individual property in a model schema.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait StoredPropertySchema 
	extends PropertySchemaFactoryWrapper[PropertySchemaData, StoredPropertySchema] 
		with VersionedDocElementData 
		with StoredVersionedDocElementLike[PropertySchemaData, StoredPropertySchema]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this property schema in the database
	  */
	def access = AccessPropertySchema(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
}

