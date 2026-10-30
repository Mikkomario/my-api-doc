package vf.myapidoc.database.reader.schema

import utopia.vault.nosql.read.linked.MultiLinkedDbReader
import vf.myapidoc.database.reader.example.ExamplePropertyValueDbReader
import vf.myapidoc.model.combined.schema.PropertyExample
import vf.myapidoc.model.stored.example.ExamplePropertyValue
import vf.myapidoc.model.stored.schema.StoredPropertySchema

/**
  * Used for reading property examples from the database
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
object PropertyExampleDbReader 
	extends MultiLinkedDbReader[StoredPropertySchema, ExamplePropertyValue, 
		PropertyExample](PropertySchemaDbReader, ExamplePropertyValueDbReader, neverEmptyRight = false)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param prop     prop to wrap
	  * @param examples examples to attach
	  */
	override def combine(prop: StoredPropertySchema, examples: Seq[ExamplePropertyValue]) = 
		PropertyExample(prop, examples)
}

