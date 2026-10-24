package vf.myapidoc.model.cached

import utopia.flow.collection.immutable.Empty
import utopia.flow.collection.immutable.tree.ValueTree
import utopia.flow.collection.template.tree.{Tree, TreeLike}
import vf.myapidoc.model.enumeration.PropertyType

/**
 * Represents a property that hasn't been stored yet
 * @param name Name of this property
 * @param dataTypes Possible types of this property
 * @param referencedType Type that this property references (applicable to model/object properties)
 * @param description Description of this property
 * @param children Nested properties (applicable to model/object properties)
 * @param possibleValues Possible values for this property (applicable to enum properties)
 * @param isOptional Whether this property may be omitted from the input/output
 * @param isArray Whether this property may contain more than one value
 * @author Mikko Hilpinen
 * @since 16.10.2026, v0.1
 */
case class NewProperty(name: String, dataTypes: Seq[PropertyType] = Empty, referencedType: String = "",
                       description: String = "", override val children: Seq[NewProperty] = Empty,
                       possibleValues: Map[String, ValueTree[String]] = Map(),
                       isOptional: Boolean = false, isArray: Boolean = false)
	extends Tree with TreeLike[NewProperty]
{
	override def self: NewProperty = this
}