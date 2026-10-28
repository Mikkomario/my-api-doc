package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for api node group-related factories that allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNodeGroupFactory[+A] extends VersionedDocElementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param apiId New api id to assign
	  * @return Copy of this item with the specified api id
	  */
	def withApiId(apiId: Int): A
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

