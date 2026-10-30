package vf.myapidoc.model.factory.example

import utopia.flow.generic.model.immutable.Value

/**
  * Common trait for example property value-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait ExamplePropertyValueFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param propId New prop id to assign
	  * @return Copy of this item with the specified prop id
	  */
	def withPropId(propId: Int): A
	
	/**
	  * @param responseId New response id to assign
	  * @return Copy of this item with the specified response id
	  */
	def withResponseId(responseId: Int): A
	
	/**
	  * @param value New value to assign
	  * @return Copy of this item with the specified value
	  */
	def withValue(value: Value): A
}

