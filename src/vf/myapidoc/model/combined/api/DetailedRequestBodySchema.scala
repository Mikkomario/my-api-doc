package vf.myapidoc.model.combined.api

import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.RequestBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.RequestBodySchema

object DetailedRequestBodySchema
{
	def apply(schema: RequestBodySchema, modelType: DetailedModelSchema): DetailedRequestBodySchema =
		DetailedRequestBodySchema(schema.id, schema.data, modelType)
}

/**
 * Includes detailed model schema data with a request body schema
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedRequestBodySchema(id: Int, data: RequestBodySchemaData, modelType: DetailedModelSchema)
	extends RequestBodySchema with RequestBodySchemaFactoryWrapper[RequestBodySchemaData, DetailedRequestBodySchema]
{
	override protected def wrap(factory: RequestBodySchemaData): DetailedRequestBodySchema = copy(data = factory)
	override def withId(id: Int): RequestBodySchema = copy(id = id)
}
