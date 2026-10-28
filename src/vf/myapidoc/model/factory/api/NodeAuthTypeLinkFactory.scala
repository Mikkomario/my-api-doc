package vf.myapidoc.model.factory.api

/**
  * Common trait for node auth type link-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthTypeLinkFactory[+A] extends NodeAuthLinkFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param authTypeId New auth type id to assign
	  * @return Copy of this item with the specified auth type id
	  */
	def withAuthTypeId(authTypeId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withAuthId(authId: Int) = withAuthTypeId(authId)
}

