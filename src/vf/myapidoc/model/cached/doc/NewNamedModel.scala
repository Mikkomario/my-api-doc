package vf.myapidoc.model.cached.doc

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

/**
 * Represents a named object schema that hasn't been stored yet
 * @param name Name of this model
 * @param properties Properties of this model
 * @param description A description of this model. May be empty.
 * @author Mikko Hilpinen
 * @since 16.10.2026, v0.1
 */
case class NewNamedModel(name: String, properties: Seq[NewProperty], description: String = "") extends ModelConvertible
{
	// IMPLEMENTED  --------------------
	
	override def toModel: Model = Model.from("name" -> name, "description" -> description, "properties" -> properties)
	
	
	// OTHER    ------------------------
	
	def contains(propName: String) = properties.exists { _.name == propName }
}