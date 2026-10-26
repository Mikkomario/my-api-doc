package vf.myapidoc.model.cached

import utopia.flow.collection.immutable.Empty
import utopia.flow.collection.template.MapAccess
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.util.StringExtensions._

/**
 * Represents an API node documentation that's not yet been stored.
 * @param name Name of this node
 * @param path This node's path, relative to the parent node or the root node if no parent exists.
 *             May contain path parameters.
 * @param parentName Name of this node's parent. Empty if not applicable.
 * @param pathParams Accepted path parameters (on this level)
 * @param tokenOptions List of accepted authorization methods. If empty, options may be inherited from the parent.
 *                     May also be overridden by individual method implementations.
 * @param scope Authorization scopes that grant access to this node.
 *              May be empty, in which case the scope may be inherited from the parent.
 *              The method implementations may also override this scope.
 * @param methodImplementations Method implementation documentation
 * @param description A description of this node / its function
 * @author Mikko Hilpinen
 * @since 21.10.2026, v0.1
 */
case class NewApiNode(name: String, path: String, parentName: String = "", pathParams: Seq[NewProperty] = Empty,
                      tokenOptions: Seq[(String, String)] = Empty, scope: Seq[(Seq[String], String)] = Empty,
                      methodImplementations: Seq[NewMethodImplementation] = Empty, description: String = "")
	extends ModelConvertible
{
	// COMPUTED -------------------------
	
	def tokenTypeNamesIterator = tokenOptions.iterator.map { _._1 } ++
		methodImplementations.iterator.flatMap { _.tokenTypeNamesIterator }
	def scopeNamesIterator = scope.iterator.flatMap { _._1 } ++
		methodImplementations.iterator.flatMap { _.scopeNamesIterator }
	
	def allTokenOptions =
		tokenOptions.view.map { case (name, description) => (None, name, description) } ++
			methodImplementations.view.flatMap { impl =>
				impl.tokenOptions.view.map { case (name, description) => (Some(impl.method), name, description) }
			}
			
	def allScopes =
		scope.view.map { case (scopes, description) => (None, scopes, description) } ++
			methodImplementations.view.flatMap { impl =>
				impl.scopes.view.map { case (scopes, description) => (Some(impl.method), scopes, description) }
			}
	
	
	// IMPLEMENTED  ---------------------
	
	override def toModel: Model = Model.from("name" -> name, "parent" -> parentName,
		"path" -> Model.from("relative" -> path, "params" -> pathParams),
		"description" -> description,
		"auth" -> Model.from(
			"tokens" -> tokenOptions.map { case (tokenType, description) =>
				Model.from("type" -> tokenType, "description" -> description)
			},
			"scope" -> scope.map { case (parts, description) =>
				Model.from("parts" -> parts, "description" -> description)
			},
		),
		"implementations" -> methodImplementations)
	
	
	// OTHER    -------------------------
			
	def fullPathUsing(nodeByName: MapAccess[String, NewApiNode]): String = parentName.ifNotEmpty match {
		case Some(parentName) => s"${ nodeByName(parentName).fullPathUsing(nodeByName) }/$path"
		case None => path
	}
}