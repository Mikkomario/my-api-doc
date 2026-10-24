package vf.myapidoc.database.reader.schema

import utopia.vault.nosql.read.linked.MultiLinkedDbReader
import vf.myapidoc.model.combined.schema.CompleteModelSchema
import vf.myapidoc.model.stored.schema.{ModelSchema, StoredPropertySchema}

/**
  * Used for reading complete model schemas from the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object CompleteModelSchemaDbReader 
	extends MultiLinkedDbReader[ModelSchema, StoredPropertySchema, CompleteModelSchema](ModelSchemaDbReader, 
		PropertySchemaDbReader, neverEmptyRight = true)
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param model      model to wrap
	  * @param properties properties to attach
	  */
	override def combine(model: ModelSchema, properties: Seq[StoredPropertySchema]) = 
		CompleteModelSchema(model, properties)
}

