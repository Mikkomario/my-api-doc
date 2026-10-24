package vf.myapidoc.model.combined.api

import vf.myapidoc.model.factory.api.PossibleResponseStatusFactoryWrapper
import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.PossibleResponseStatus

/**
  * Combines status with body data
  * @tparam Body Type of the wrapped body
 * @tparam Repr Implementing type
 * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PossibleResponseLike[+Body, +Repr <: PossibleResponseStatus]
	extends PossibleResponseStatus with PossibleResponseStatusFactoryWrapper[PossibleResponseStatusData, Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * The body that is attached to this status.
	 * None if no body is included or if no body has been documented.
	  */
	def body: Option[Body]
}

