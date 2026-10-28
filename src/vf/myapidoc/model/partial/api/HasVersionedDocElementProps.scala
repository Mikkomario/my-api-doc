package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

/**
  * Common trait for classes that provide access to versioned doc element properties
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait HasVersionedDocElementProps extends ModelConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * ID of the update that introduced this element (version)
	  */
	def originalUpdateId: Int
	/**
	  * ID of the update that removed or replaced this element (version)
	  */
	def removingUpdateId: Option[Int]
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Pair("originalUpdateId" -> originalUpdateId, "removingUpdateId" -> removingUpdateId))
}

