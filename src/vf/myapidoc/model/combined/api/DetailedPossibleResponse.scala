package vf.myapidoc.model.combined.api

import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.PossibleResponseStatus

object DetailedPossibleResponse
{
	def apply(status: PossibleResponseStatus, body: Option[DetailedResponseContentType]): DetailedPossibleResponse =
		DetailedPossibleResponse(status.id, status, body)
}

/**
 * Includes detailed response body information with a response status code entry
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedPossibleResponse(id: Int, data: PossibleResponseStatusData,
                                    body: Option[DetailedResponseContentType] = None)
	extends PossibleResponseLike[DetailedResponseContentType, DetailedPossibleResponse]
{
	override def withId(id: Int): PossibleResponseStatus = copy(id = id)
	override protected def wrap(factory: PossibleResponseStatusData): DetailedPossibleResponse = copy(data = factory)
}