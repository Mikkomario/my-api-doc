package vf.myapidoc.model.combined.api

import utopia.access.model.enumeration.Method
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.{Model, Value}
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import vf.myapidoc.model.combined.schema.DetailedPropertySchema
import vf.myapidoc.model.factory.api.ApiNodeFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeData
import vf.myapidoc.model.stored.api.{ApiNode, StoredVersionedDocElementLike}
import vf.myapidoc.model.template.ClientModelConvertible

/**
 * Includes full API node documentation
 * @param id ID of this node
 * @param data Wrapped node data
 * @param relativePaths Direct path definitions that apply to this node
 * @param implementations Method implementations in this node
 * @param definedTokenTypes Accepted token types, if defined on this level (parents may have defined some token types)
 * @param definedScopes Accepted auth scopes, if defined on this level (parents may have defined some scopes)
 * @param parents Nodes that appear before this one in the path. Ordered from bottom to top.
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedApiNode(id: Int, data: ApiNodeData, relativePaths: Seq[DetailedApiNodePath],
                           implementations: Seq[DetailedApiNodeMethodImplementation],
                           definedTokenTypes: Seq[DetailedNodeAuthTypeLink],
                           definedScopes: Seq[DetailedNodeAuthScopeLink], parents: Seq[DetailedApiNode])
	extends ApiNode with ApiNodeFactoryWrapper[ApiNodeData, DetailedApiNode] with ClientModelConvertible
		with StoredVersionedDocElementLike[ApiNodeData, DetailedApiNode]
{
	// ATTRIBUTES   -----------------------
	
	/**
	 * Absolute paths from the API root to this node.
	 * Each entry contains two values:
	 *      1. The path as a string
	 *      1. Accepted path parameters
	 */
	lazy val absolutePaths: Seq[(String, Seq[DetailedPropertySchema])] = parent match {
		case Some(parent) =>
			parent.absolutePaths.flatMap { case (parentPath, parentParams) =>
				relativePaths.iterator.map { path =>
					val pathStr = s"${ parentPath.notEndingWith("/") }${ path.relative.startingWith("/") }"
					val params = path.params match {
						case Some(params) => parentParams ++ params.properties
						case None => parentParams
					}
					pathStr -> params
				}
			}
		case None =>
			relativePaths.map { path =>
				val params = path.params match {
					case Some(params) => params.properties
					case None => Empty
				}
				path.relative -> params
			}
	}
	/**
	 * Token types accepted by this node
	 */
	lazy val tokenTypes = definitionsIter.map { _.definedTokenTypes }.find { _.nonEmpty }.getOrElse(Empty)
	/**
	 * Scopes accepted by this node
	 */
	lazy val scopes = definitionsIter.map { _.definedScopes }.find { _.nonEmpty }.getOrElse(Empty)
	
	
	// COMPUTED ---------------------------
	
	/**
	 * @return The direct parent of this node. None if no node appears between this node and the root node.
	 */
	def parent = parents.headOption
	
	/**
	 * @return An iterator that yields names of all sensitive properties listed in this node's documentation.
	 *         Doesn't include properties defined in parent nodes.
	 */
	def sensitivePropertyNamesIterator =
		implementations.iterator.flatMap { _.sensitivePropertyNamesIterator } ++
			relativePaths.iterator.flatMap { _.params }.flatMap { _.sensitivePropertyNamesIterator }
	
	private def definitionsIter = Iterator.single(this) ++ parents.iterator
	
	
	// IMPLEMENTED  -----------------------
	
	override def toModel = super.toModel ++ Model.from(
		"parent" -> parent.map { p => Model.from("id" -> p.id, "name" -> p.name) }, "paths" -> relativePaths,
		"directAuth" -> Model.from("tokens" -> definedTokenTypes, "scopes" -> definedScopes),
		"implementations" -> implementations)
	override def toClientModel: Model = Model
		.apply(Vector.concat[(String, Value)](
			Vector("id" -> id, "name" -> data.name, "description" -> data.description),
			// Path and path parameters are represented either with two separate properties,
			// if there's only one variant.
			// Alternatively, includes a "paths" property that lists all alternative path versions.
			absolutePaths.oneOrMany match {
				case Left((path, params)) =>
					if (params.isEmpty)
						Single("path" -> path)
					else
						Pair("path" -> path, "pathParams" -> params)
				case Right(paths) =>
					Single("paths" ->
						paths.map { case (path, params) => Model.from("path" -> path, "params" -> params) })
			},
			Single("actions" -> implementations.map { impl =>
				// Request body optionality is only included if it couldn't be inferred from the body entry
				// The body is represented using either "requestBody" or "requestBodyOptions",
				// depending on whether there are alternative formats
				val requestBodyPart: Seq[(String, Value)] = impl.requestBodyOptions.emptyOneOrMany match {
					case None =>
						if (impl.requiresRequestBody)
							Single("requiresRequestBody" -> true)
						else
							Empty
					case Some(Left(body)) =>
						if (impl.requiresRequestBody)
							Single("requestBody" -> body.toClientModel)
						else
							Pair("requestBody" -> body.toClientModel, "requiresRequestBody" -> false)
					
					case Some(Right(options)) =>
						if (impl.requiresRequestBody)
							Single("requestBodyOptions" -> options.map { _.toClientModel })
						else
							Pair("requestBodyOptions" -> options.map { _.toClientModel },
								"requiresRequestBody" -> false)
				}
				// Includes token types & scopes defined in the parent(s)
				val tokenTypes = NotEmpty(this.tokenTypes.filter { _.method.contains(impl.method) }).getOrElse {
					this.tokenTypes.filter { _.method.isEmpty }
				}
				val scopes = NotEmpty(this.scopes.filter { _.method.contains(impl.method) }).getOrElse {
					this.scopes.filter { _.method.isEmpty }
				}
				// May include "token" or "tokens"
				val tokensPart: Seq[(String, Value)] = tokenTypes.emptyOneOrMany match {
					case None => Empty
					case Some(Left(token)) => Single("token" -> token.toClientModel)
					case Some(Right(tokens)) => Single("tokens" -> tokens.map { _.toClientModel })
				}
				// May include "scope", "scopes" or "scopeOptions"
				val scopesPart: Seq[(String, Value)] = scopes.emptyOneOrMany match {
					case None => Empty
					case Some(Left(scope)) =>
						val (scopeKey, scopeValue) = scope.scopes.oneOrMany match {
							case Left(scope) => "scope" -> (scope.name: Value)
							case Right(scopes) => "scopes" -> (scopes.map { _.name }: Value)
						}
						if (scope.data.description.isEmpty)
							Single(scopeKey -> scopeValue)
						else
							Pair(scopeKey -> scopeValue,
								(if (tokensPart.isEmpty) "description" else "scopeDescription") -> scope.data.description)
					
					case Some(Right(scopeOptions)) =>
						if (scopeOptions.forall { s => s.data.description.isEmpty && s.scopes.hasSize(1) })
							Single("scopeOptions" -> scopeOptions.map { _.scopes.head.name })
						else
							Single("scopeOptions" -> scopeOptions.map { _.toClientModel })
				}
				Model
					.apply(Vector.concat(
						Vector("method" -> impl.method.name, "description" -> impl.data.description),
						// Tokens and scopes are included under "auth"
						if (tokenTypes.nonEmpty || scopes.nonEmpty)
							Single("auth" -> Model.apply(tokensPart ++ scopesPart))
						else
							Empty,
						impl.queryParams match {
							case Some(params) => Single("queryParams" -> params.properties.map { _.toClientModel })
							case None => Empty
						},
						requestBodyPart,
						if (impl.responses.isEmpty)
							Empty
						else
							Single("responses" -> impl.responses.map { _.toClientModel })
					))
					.withoutEmptyValues
			})
		))
		.withoutEmptyValues
	
	override def wrap(factory: ApiNodeData): DetailedApiNode = copy(data = factory)
	override def withId(id: Int) = copy(id = id)
	
	
	// OTHER    -------------------------
	
	def apply(method: Method) =
		get(method).getOrElse { throw new NoSuchElementException(s"${
			data.name } doesn't contain an implementation for ${ method.name }") }
	def get(method: Method) = implementations.find { _.method == method }
}
