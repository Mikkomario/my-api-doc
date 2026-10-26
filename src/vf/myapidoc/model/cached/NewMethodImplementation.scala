package vf.myapidoc.model.cached

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.util.UncertainBoolean.{CertainlyFalse, CertainlyTrue}
import utopia.flow.util.{Mutate, UncertainBoolean}

/**
 * Contains non-stored information about an API node's method implementation.
 * @param method The implemented method
 * @param tokenOptions Types of authorization tokens usable for this action.
 *                     May include descriptions.
 *                     If empty, may be specified in the node, or in one of the parent nodes.
 * @param scopes Authorization scopes that provide access to this feature.
 *               May contain multiple alternatives.
 *               Each may contain more than one required entry.
 *               May contain descriptions.
 *               If empty, may be specified in the node, or in one of the parent nodes.
 * @param queryParams Accepted query parameters
 * @param requestBodyOptions Accepted request body.
 *                           May contain multiple alternative options.
 * @param statusCodes Expected response status codes. Each entry may contain a description.
 * @param responseBody Documented response body format. Empty if no response body is included,
 *                     or if not documented.
 * @param description A description of this method implementation / feature
 * @param responseHasBody Whether the successful response is known to yield a body.
 *                        Uncertain if not documented.
 * @param requestBodyIsOptional Whether the request body may be omitted altogether.
 * @author Mikko Hilpinen
 * @since 19.10.2026, v0.1
 */
case class NewMethodImplementation(method: Method, tokenOptions: Seq[(String, String)] = Empty,
                                   scopes: Seq[(Seq[String], String)] = Empty,
                                   queryParams: Seq[NewProperty] = Empty,
                                   requestBodyOptions: Seq[NewBodyDefinition] = Empty,
                                   statusCodes: Seq[(Int, String)] = Empty,
                                   responseBody: Option[NewBodyDefinition] = None, description: String = "",
                                   responseHasBody: UncertainBoolean = UncertainBoolean,
                                   requestBodyIsOptional: Boolean = false)
	extends ModelConvertible
{
	// COMPUTED -------------------------
	
	def tokenTypeNamesIterator = tokenOptions.iterator.map { _._1 }
	def scopeNamesIterator = scopes.iterator.flatMap { _._1 }
	
	/**
	 * @return All documented responses from this implementation.
	 *         Each entry contains:
	 *         1. Status code
	 *         1. Response body description, if documented
	 *         1. Responsee (status) description
	 *         1. Whether a response body is included
	 */
	def responses = {
		val defaultResult = statusCodes.map { case (statusCode, description) =>
			val (body, hasBody) = {
				// Case: Success response => Uses the implementation's body information
				if (statusCode < 300) {
					// Case: 204 => Never contains a body
					if (statusCode == 204)
						None -> CertainlyFalse
					else
						responseBody -> responseHasBody
				}
				// Case: Failure response => Won't document a body
				else
					None -> UncertainBoolean
			}
			(statusCode, body, description, hasBody)
		}
		// Ensures that the response body gets documented somehow, if defined
		if (statusCodes.forall { _._1 >= 300 } && responseBody.isDefined) {
			val additionalEntry = (200, responseBody, "", CertainlyTrue)
			additionalEntry +: defaultResult
		}
		else
			defaultResult
	}
	
	
	// IMPLEMENTED  ---------------------
	
	override def toModel: Model = Model.from("method" -> method.name, "description" -> description,
		"auth" -> Model.from(
			"tokens" -> tokenOptions.map { case (tokenType, desc) =>
				Model.from("type" -> tokenType, "description" -> desc)
			},
			"scope" -> scopes.map { case (parts, description) =>
				Model.from("parts" -> parts, "description" -> description)
			}),
		"query" -> queryParams, "requiresRequestBody" -> !requestBodyIsOptional, "requestBody" -> requestBodyOptions,
		"responses" -> responses.map { case (status, body, description, hasBody) =>
			Model.from("status" -> status, "description" -> description, "hasBody" -> hasBody, "body" -> body)
		})
	
	
	// OTHER ----------------------------
	
	def withMethod(method: Method) = copy(method = method)
	def withDescription(description: String) = copy(description = description)
	
	def mapDescription(f: Mutate[String]) = withDescription(f(description))
}