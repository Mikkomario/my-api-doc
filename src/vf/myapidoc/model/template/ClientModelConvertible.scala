package vf.myapidoc.model.template

import utopia.flow.generic.model.immutable.Model

/**
 * Common trait for items that may be converted into client-friendly models
 * @author Mikko Hilpinen
 * @since 27.10.2026, v0.1
 */
trait ClientModelConvertible
{
	/**
	 * @return A client-friendly model representation of this instance
	 */
	def toClientModel: Model
}
