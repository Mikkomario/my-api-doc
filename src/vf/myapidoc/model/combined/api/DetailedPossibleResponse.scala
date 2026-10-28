package vf.myapidoc.model.combined.api

import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Constant, Model, Value}
import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.{PossibleResponseStatus, StoredVersionedDocElementLike}
import vf.myapidoc.model.template.ClientModelConvertible

object DetailedPossibleResponse
{
	def apply(status: PossibleResponseStatus, body: Option[DetailedResponseBodySchema]): DetailedPossibleResponse =
		DetailedPossibleResponse(status.id, status, body)
}

/**
 * Includes detailed response body information with a response status code entry
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedPossibleResponse(id: Int, data: PossibleResponseStatusData,
                                    body: Option[DetailedResponseBodySchema] = None)
	extends PossibleResponseLike[DetailedResponseBodySchema, DetailedPossibleResponse] with ClientModelConvertible
		with StoredVersionedDocElementLike[PossibleResponseStatusData, DetailedPossibleResponse]
{
	override def toModel = super.toModel + Constant("body", body)
	override def toClientModel: Model = Model
		.apply(Vector.concat(
			Single("status" -> data.statusCode),
			if (data.hasBody.isCertainlyFalse) Single("empty" -> true) else Empty,
			if (body.isEmpty && data.hasBody.isCertainlyTrue) Single("empty" -> false) else Empty,
			Single("description" -> data.description),
			body.map { b => "body" -> (b.toClientModel: Value) }.emptyOrSingle
		))
		.withoutEmptyValues
	
	override def withId(id: Int) = copy(id = id)
	override protected def wrap(factory: PossibleResponseStatusData): DetailedPossibleResponse = copy(data = factory)
}