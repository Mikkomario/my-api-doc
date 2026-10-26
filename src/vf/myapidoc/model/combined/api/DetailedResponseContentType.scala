package vf.myapidoc.model.combined.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Constant
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.ResponseContentTypeFactoryWrapper
import vf.myapidoc.model.partial.api.ResponseContentTypeData
import vf.myapidoc.model.stored.api.ResponseContentType

object DetailedResponseContentType
{
	def apply(contentType: ResponseContentType, modelType: Option[DetailedModelSchema]): DetailedResponseContentType =
		DetailedResponseContentType(contentType.id, contentType.data, modelType)
}

/**
 * Includes detailed information about response body models with a response content type instance, where applicable.
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedResponseContentType(id: Int, data: ResponseContentTypeData,
                                       modelType: Option[DetailedModelSchema] = None)
	extends ResponseContentType
		with ResponseContentTypeFactoryWrapper[ResponseContentTypeData, DetailedResponseContentType]
{
	override def toModel = super.toModel + Constant("content", modelType)
	
	override def withId(id: Int): ResponseContentType = copy(id = id)
	override protected def wrap(factory: ResponseContentTypeData): DetailedResponseContentType = copy(data = factory)
}