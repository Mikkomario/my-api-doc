package vf.myapidoc.model.factory.api

import java.time.Instant

/**
  * Common trait for api node-related factories which allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait ApiNodeFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param deprecatedAfter New deprecated after to assign
	  * @return Copy of this item with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant): A
	
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	
	/**
	  * @param groupId New group id to assign
	  * @return Copy of this item with the specified group id
	  */
	def withGroupId(groupId: Int): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
	
	/**
	  * @param nameNoSpaces New name no spaces to assign
	  * @return Copy of this item with the specified name no spaces
	  */
	def withNameNoSpaces(nameNoSpaces: String): A
	
	/**
	  * @param parentId New parent id to assign
	  * @return Copy of this item with the specified parent id
	  */
	def withParentId(parentId: Int): A
	
	/**
	  * @param relativePath New relative path to assign
	  * @return Copy of this item with the specified relative path
	  */
	def withRelativePath(relativePath: String): A
}

