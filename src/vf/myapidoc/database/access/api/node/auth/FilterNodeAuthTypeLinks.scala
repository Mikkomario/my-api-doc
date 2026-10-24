package vf.myapidoc.database.access.api.node.auth

import vf.myapidoc.database.storable.api.NodeAuthTypeLinkDbModel

/**
  * Common trait for access points which may be filtered based on node auth type link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterNodeAuthTypeLinks[+Repr] extends FilterNodeAuthLinks[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines node auth type link database properties
	  */
	def model = NodeAuthTypeLinkDbModel
}

