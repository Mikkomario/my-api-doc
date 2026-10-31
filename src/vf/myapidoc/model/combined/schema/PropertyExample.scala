package vf.myapidoc.model.combined.schema

import vf.myapidoc.model.factory.schema.PropertySchemaFactoryWrapper
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike
import vf.myapidoc.model.stored.example.ExamplePropertyValue
import vf.myapidoc.model.stored.schema.StoredPropertySchema

object PropertyExample
{
	// OTHER	--------------------
	
	/**
	  * @param prop     The prop to wrap
	  * @param examples examples to attach
	  * @return Combination of the specified prop and example
	  */
	def apply(prop: StoredPropertySchema, examples: Seq[ExamplePropertyValue]): PropertyExample = 
		apply(prop.id, prop.data, examples)
	
	/**
	  * @param id       ID of this prop in the DB
	  * @param data     The wrapped prop data
	  * @param examples examples to attach
	  * @return A new prop with the specified example included
	  */
	def apply(id: Int, data: PropertySchemaData, examples: Seq[ExamplePropertyValue]): PropertyExample = 
		_PropertyExample(id, data, examples)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id       ID of this prop in the DB
	  * @param data     The wrapped prop data
	  * @param examples examples to attach
	  */
	private case class _PropertyExample(id: Int, data: PropertySchemaData, examples: Seq[ExamplePropertyValue])
		extends PropertyExample
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: PropertySchemaData) = copy(data = factory)
	}
}

/**
  * Includes value examples with a property schema
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait PropertyExample 
	extends StoredPropertySchema with PropertySchemaFactoryWrapper[PropertySchemaData, PropertyExample]
		with StoredVersionedDocElementLike[PropertySchemaData, PropertyExample]
{
	// ABSTRACT	--------------------
	
	/**
	  * Examples that are attached to this prop
	  */
	def examples: Seq[ExamplePropertyValue]
}

