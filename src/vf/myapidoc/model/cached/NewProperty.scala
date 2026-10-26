package vf.myapidoc.model.cached

import utopia.flow.collection.immutable.Empty
import utopia.flow.collection.template.tree.{Tree, TreeLike}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.enumeration.PropertyType

/**
 * Represents a property that hasn't been stored yet
 * @param name Name of this property
 * @param dataTypes Possible types of this property
 * @param referencedType Type that this property references (applicable to model/object properties)
 * @param description Description of this property
 * @param children Nested properties (applicable to model/object properties)
 * @param possibleValues Possible values for this property (applicable to enum properties).
 *                       Each is combined with an optional description.
 * @param isOptional Whether this property may be omitted from the input/output
 * @param isArray Whether this property may contain more than one value
 * @author Mikko Hilpinen
 * @since 16.10.2026, v0.1
 */
case class NewProperty(name: String, dataTypes: Seq[PropertyType] = Empty, referencedType: String = "",
                       description: String = "", override val children: Seq[NewProperty] = Empty,
                       possibleValues: Seq[(String, String)] = Empty,
                       isOptional: Boolean = false, isArray: Boolean = false)
	extends Tree with TreeLike[NewProperty] with ModelConvertible
{
	override def self: NewProperty = this
	
	override def toModel: Model = Model.from("name" -> name, "types" -> dataTypes.map { _.name },
		"array" -> isArray, "optional" -> isOptional,
		"options" -> possibleValues.map { case (value, desc) =>
			Model.from("value" -> value, "description" -> desc)
		},
		"references" -> referencedType, "description" -> description, "children" -> children)
}