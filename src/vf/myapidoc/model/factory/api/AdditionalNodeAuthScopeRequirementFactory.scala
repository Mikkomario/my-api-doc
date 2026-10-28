package vf.myapidoc.model.factory.api

/**
  * Common trait for additional node auth scope requirement-related factories that allow 
  * construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AdditionalNodeAuthScopeRequirementFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param scopeId New scope id to assign
	  * @return Copy of this item with the specified scope id
	  */
	def withScopeId(scopeId: Int): A
	/**
	  * @param scopeLinkId New scope link id to assign
	  * @return Copy of this item with the specified scope link id
	  */
	def withScopeLinkId(scopeLinkId: Int): A
}

