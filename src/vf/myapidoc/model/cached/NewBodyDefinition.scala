package vf.myapidoc.model.cached

import utopia.access.model.ContentType
import utopia.flow.collection.immutable.Empty

/**
 * Represents a request or a response body schema that has not yet been stored
 * @param properties Supported request body properties
 * @param referencedType Name of the referenced model or property type, if applicable
 * @param contentType Applicable content type, if specified
 * @param details Additional details about the body, if specified
 * @param receivesArray Whether an (object) array is expected. False (default) if a single object is expected instead.
 * @author Mikko Hilpinen
 * @since 19.10.2026, v0.1
 */
case class NewBodyDefinition(properties: Seq[NewProperty] = Empty, referencedType: String = "",
                             contentType: Option[ContentType] = None, details: String = "",
                             receivesArray: Boolean = false)
