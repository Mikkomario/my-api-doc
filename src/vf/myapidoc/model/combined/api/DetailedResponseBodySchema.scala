package vf.myapidoc.model.combined.api

import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.ResponseBodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.ResponseBodySchemaData
import vf.myapidoc.model.stored.api.{ResponseBodySchema, StoredBodySchemaLike}
import vf.myapidoc.model.template.ClientModelConvertible

object DetailedResponseBodySchema
{
	def apply(body: ResponseBodySchema, modelType: Option[DetailedModelSchema]): DetailedResponseBodySchema =
		DetailedResponseBodySchema(body.id, body.data, modelType)
}

/**
 * Includes detailed information about response body models with a response content type instance, where applicable.
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedResponseBodySchema(id: Int, data: ResponseBodySchemaData,
                                      modelType: Option[DetailedModelSchema] = None)
	extends ResponseBodySchema with StoredBodySchemaLike[ResponseBodySchemaData, DetailedResponseBodySchema]
		with ResponseBodySchemaFactoryWrapper[ResponseBodySchemaData, DetailedResponseBodySchema]
		with ClientModelConvertible
{
	override def toModel = super.toModel + Constant("content", modelType)
	override def toClientModel: Model = {
		val base = Model(Vector.concat[(String, Value)](
			Single("type" -> data.dataType.name),
			if (data.isArray) Single("array" -> true) else Empty
		))
		modelType match {
			case Some(content) => base ++ content.toClientModel
			case None => base
		}
	}
	
	override def withId(id: Int): DetailedResponseBodySchema = copy(id = id)
	override protected def wrap(factory: ResponseBodySchemaData): DetailedResponseBodySchema = copy(data = factory)
}