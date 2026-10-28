package vf.myapidoc.model.combined.api

import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.{PossibleResponseStatus, ResponseBodySchema}

object PossibleResponse
{
	// OTHER	--------------------
	
	/**
	  * @param status The status to wrap
	  * @param body   body to attach
	  * @return Combination of the specified status and body
	  */
	def apply(status: PossibleResponseStatus, body: Option[ResponseBodySchema]): PossibleResponse =
		apply(status.id, status.data, body)
	/**
	  * @param id   ID of this status in the DB
	  * @param data The wrapped status data
	  * @param body body to attach
	  * @return A new status with the specified body included
	  */
	def apply(id: Int, data: PossibleResponseStatusData, body: Option[ResponseBodySchema]): PossibleResponse =
		_PossibleResponse(id, data, body)
	
	
	// NESTED	--------------------
	
	/**
	  * @param id   ID of this status in the DB
	  * @param data The wrapped status data
	  * @param body body to attach
	  */
	private case class _PossibleResponse(id: Int, data: PossibleResponseStatusData, body: Option[ResponseBodySchema])
		extends PossibleResponse
	{
		// IMPLEMENTED	--------------------
		
		override def withId(id: Int) = copy(id = id)
		
		override protected def wrap(factory: PossibleResponseStatusData) = copy(data = factory)
	}
}

/**
  * Combines status with body data
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PossibleResponse
	extends PossibleResponseLike[ResponseBodySchema, PossibleResponse]

