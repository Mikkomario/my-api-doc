package vf.myapidoc.model.cached

import utopia.access.model.ContentType
import utopia.flow.collection.immutable.Empty
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.operator.MaybeEmpty
import vf.myapidoc.model.enumeration.PropertyType

/**
 * Represents a request or a response body schema that has not yet been stored
 * @param dataType Type of this body's contents
 * @param properties Supported request body properties
 * @param referencedType Name of the referenced model or property type, if applicable
 * @param contentType Applicable content type, if specified
 * @param details Additional details about the body, if specified
 * @param isArray Whether an (object) array is expected or returned.
 *                False (default) if a single object is expected or returned instead.
 * @author Mikko Hilpinen
 * @since 19.10.2026, v0.1
 */
case class NewBodyDefinition(dataType: PropertyType, properties: Seq[NewProperty] = Empty, referencedType: String = "",
                             contentType: Option[ContentType] = None, details: String = "", isArray: Boolean = false)
	extends MaybeEmpty[NewBodyDefinition] with ModelConvertible
{
	override def self: NewBodyDefinition = this
	
	override def isEmpty: Boolean = properties.isEmpty && referencedType.isEmpty
	
	override def toModel: Model = Model.from("description" -> details, "contentType" -> contentType.map { _.toString },
		"array" -> isArray, "references" -> referencedType, "properties" -> properties)
}