package vf.myapidoc.model.combined.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Constant
import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.PossibleResponseStatus

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
	extends PossibleResponseLike[DetailedResponseBodySchema, DetailedPossibleResponse]
{
	override def toModel = super.toModel + Constant("body", body)
	
	override def withId(id: Int): PossibleResponseStatus = copy(id = id)
	override protected def wrap(factory: PossibleResponseStatusData): DetailedPossibleResponse = copy(data = factory)
}