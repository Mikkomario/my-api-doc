package vf.myapidoc.model.cached

import utopia.access.model.enumeration.Method
import utopia.flow.collection.immutable.Empty
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
{
	def tokenTypeNamesIterator = tokenOptions.iterator.map { _._1 }
	def scopeNamesIterator = scopes.iterator.flatMap { _._1 }
	
	def withMethod(method: Method) = copy(method = method)
	def withDescription(description: String) = copy(description = description)
	
	def mapDescription(f: Mutate[String]) = withDescription(f(description))
}