package vf.myapidoc.model.combined.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationData
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementation

object DetailedApiNodeMethodImplementation
{
	def apply(implementation: ApiNodeMethodImplementation, queryParams: Option[DetailedModelSchema],
	          requestBodyOptions: Seq[DetailedRequestBodySchema],
	          responses: Seq[DetailedPossibleResponse]): DetailedApiNodeMethodImplementation =
		DetailedApiNodeMethodImplementation(implementation.id, implementation.data, queryParams, requestBodyOptions,
			responses)
}

/**
 * Includes full API node method implementation information
 * @param id ID of this implementation
 * @param data Wrapped implementation details
 * @param queryParams Accepted query params. None if not applicable.
 * @param requestBodyOptions Accepted request body options.
 * @param responses Documented response status codes with possibly documented response bodies
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedApiNodeMethodImplementation(id: Int, data: ApiNodeMethodImplementationData,
                                               queryParams: Option[DetailedModelSchema],
                                               requestBodyOptions: Seq[DetailedRequestBodySchema],
                                               responses: Seq[DetailedPossibleResponse])
	extends ApiNodeMethodImplementation
		with ApiNodeMethodImplementationFactoryWrapper[ApiNodeMethodImplementationData, DetailedApiNodeMethodImplementation]
{
	override def toModel = super.toModel ++ Model.from(
		"queryParams" -> queryParams, "requestBody" -> requestBodyOptions, "responses" -> responses)
	
	override protected def wrap(factory: ApiNodeMethodImplementationData): DetailedApiNodeMethodImplementation =
		copy(data = factory)
	override def withId(id: Int): ApiNodeMethodImplementation = copy(id = id)
}