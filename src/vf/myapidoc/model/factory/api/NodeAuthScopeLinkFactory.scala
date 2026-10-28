package vf.myapidoc.model.factory.api

/**
  * Common trait for node auth scope link-related factories that allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthScopeLinkFactory[+A] extends NodeAuthLinkFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param scopeId New scope id to assign
	  * @return Copy of this item with the specified scope id
	  */
	def withScopeId(scopeId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withAuthId(authId: Int) = withScopeId(authId)
}

