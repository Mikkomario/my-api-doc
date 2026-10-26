package vf.myapidoc.model.combined.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Constant
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.RequestBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.{RequestBodySchema, StoredApiNodeMethodImplementationModelSchemaLinkLike}

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
	extends RequestBodySchema
		with StoredApiNodeMethodImplementationModelSchemaLinkLike[RequestBodySchemaData, DetailedRequestBodySchema]
		with RequestBodySchemaFactoryWrapper[RequestBodySchemaData, DetailedRequestBodySchema]
{
	override def toModel = super.toModel + Constant("content", modelType)
	
	override protected def wrap(factory: RequestBodySchemaData): DetailedRequestBodySchema = copy(data = factory)
	override def withId(id: Int): DetailedRequestBodySchema = copy(id = id)
}
