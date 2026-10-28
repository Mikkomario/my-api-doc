package vf.myapidoc.model.factory.schema

import vf.myapidoc.model.factory.api.VersionedDocElementFactory

import java.time.Instant

/**
  * Common trait for possible property value-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
trait PossiblePropertyValueFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	/**
	  * @param propId New prop id to assign
	  * @return Copy of this item with the specified prop id
	  */
	def withPropId(propId: Int): A
	/**
	  * @param value New value to assign
	  * @return Copy of this item with the specified value
	  */
	def withValue(value: String): A
}

