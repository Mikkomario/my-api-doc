package vf.myapidoc.model.combined.api

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.RequestBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.{RequestBodySchema, StoredBodySchemaLike}
import vf.myapidoc.model.template.ClientModelConvertible

object DetailedRequestBodySchema
{
	def apply(schema: RequestBodySchema, modelType: Option[DetailedModelSchema]): DetailedRequestBodySchema =
		DetailedRequestBodySchema(schema.id, schema.data, modelType)
}

/**
 * Includes detailed model schema data with a request body schema
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedRequestBodySchema(id: Int, data: RequestBodySchemaData, modelType: Option[DetailedModelSchema])
	extends RequestBodySchema with StoredBodySchemaLike[RequestBodySchemaData, DetailedRequestBodySchema]
		with RequestBodySchemaFactoryWrapper[RequestBodySchemaData, DetailedRequestBodySchema]
		with ClientModelConvertible
{
	override def toModel = super.toModel + Constant("content", modelType)
	override def toClientModel: Model = Model
		.apply(Vector.concat(
			Vector("contentType" -> data.customContentType, "type" -> data.dataType.name),
			if (data.isArray) Single("array" -> true) else Empty,
			modelType.map { m => "content" -> (m.toClientModel: Value) }.emptyOrSingle,
			Single("description" -> data.description)
		))
		.withoutEmptyValues
	
	override protected def wrap(factory: RequestBodySchemaData): DetailedRequestBodySchema = copy(data = factory)
	override def withId(id: Int): DetailedRequestBodySchema = copy(id = id)
}
