package vf.myapidoc.model.combined.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.casting.ValueConversions._
import vf.myapidoc.model.combined.schema.DetailedModelSchema
import vf.myapidoc.model.factory.api.ApiNodeFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeData
import vf.myapidoc.model.stored.api.ApiNode

/**
 * Includes full API node documentation
 * @param id ID of this node
 * @param data Wrapped node data
 * @param implementations Method implementations in this node
 * @param definedTokenTypes Accepted token types, if defined on this level (parents may have defined some token types)
 * @param definedScopes Accepted auth scopes, if defined on this level (parents may have defined some scopes)
 * @param directPathParams Accepted path parameters on this level
 * @param parents Nodes that appear before this one in the path. Ordered from bottom to top.
 * @author Mikko Hilpinen
 * @since 23.10.2026, v0.1
 */
case class DetailedApiNode(id: Int, data: ApiNodeData, implementations: Seq[DetailedApiNodeMethodImplementation],
                           definedTokenTypes: Seq[DetailedNodeAuthTypeLink],
                           definedScopes: Seq[DetailedNodeAuthScopeLink], directPathParams: Option[DetailedModelSchema],
                           parents: Seq[DetailedApiNode])
	extends ApiNode with ApiNodeFactoryWrapper[ApiNodeData, DetailedApiNode]
{
	// ATTRIBUTES   -----------------------
	
	/**
	 * Absolute path from the API root to this node
	 */
	val absolutePath: String = parents.headOption match {
		case Some(parent) => s"${ parent.absolutePath }/${ data.relativePath }"
		case None => data.relativePath
	}
	
	
	// IMPLEMENTED  -----------------------
	
	override def toModel = super.toModel ++ Model.from(
		"parent" -> parents.headOption.map { p => Model.from("id" -> p.id, "name" -> p.name) },
		"absolutePath" -> absolutePath, "directPathParams" -> directPathParams,
		"directAuth" -> Model.from("tokens" -> definedTokenTypes, "scopes" -> definedScopes),
		"implementations" -> implementations)
	
	override protected def wrap(factory: ApiNodeData): DetailedApiNode = copy(data = factory)
	override def withId(id: Int): ApiNode = copy(id = id)
}
