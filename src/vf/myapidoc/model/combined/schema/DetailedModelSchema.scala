package vf.myapidoc.model.combined.schema

import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.ModelSchema

object DetailedModelSchema
{
	def apply(schema: ModelSchema, properties: Seq[DetailedPropertySchema]): DetailedModelSchema =
		apply(schema.id, schema.data, properties)
}

/**
 * Includes detailed property information with a model schema
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedModelSchema(id: Int, data: ModelSchemaData, properties: Seq[DetailedPropertySchema])
	extends CompleteModelSchema with CompleteModelSchemaLike[DetailedPropertySchema, DetailedModelSchema]
{
	override def withId(id: Int): ModelSchema = copy(id = id)
	override protected def wrap(factory: ModelSchemaData): DetailedModelSchema = copy(data = factory)
}