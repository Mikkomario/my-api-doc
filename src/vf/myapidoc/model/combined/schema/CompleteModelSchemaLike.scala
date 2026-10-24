package vf.myapidoc.model.combined.schema

import vf.myapidoc.model.factory.schema.ModelSchemaFactoryWrapper
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.{ModelSchema, StoredPropertySchema}

/**
  * Includes the declared properties in a model schema.
  * @tparam P Type of the wrapped properties
 * @tparam Repr Implementing type
 * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait CompleteModelSchemaLike[+P, +Repr <: ModelSchema]
	extends ModelSchema with ModelSchemaFactoryWrapper[ModelSchemaData, Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Properties that are attached to this model
	  */
	def properties: Seq[P]
}

