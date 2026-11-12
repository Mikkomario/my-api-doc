package vf.myapidoc.model.factory.example

import utopia.flow.generic.model.immutable.Value

/**
  * Common trait for undocumented property-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
trait UndocumentedPropertyFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param modelTypeId New model type id to assign
	  * @return Copy of this item with the specified model type id
	  */
	def withModelTypeId(modelTypeId: Int): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
	
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

