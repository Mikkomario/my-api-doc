package vf.myapidoc.model.stored.api

import vf.myapidoc.database.access.api.node.path.AccessApiNodePath
import vf.myapidoc.model.factory.api.ApiNodePathFactoryWrapper
import vf.myapidoc.model.partial.api.{ApiNodePathData, VersionedDocElementData}

/**
  * Represents a api node path that has already been stored in the database. 
  * Represents a path leading to an API node. Relative to the parent node (if specified) or the 
  * API root.
  * @tparam Repr The implementing type
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait ApiNodePathLike[+Repr <: VersionedDocElementData] 
	extends ApiNodePathFactoryWrapper[ApiNodePathData, Repr] with VersionedDocElementData 
		with StoredVersionedDocElementLike[ApiNodePathData, Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this api node path in the database
	  */
	def access = AccessApiNodePath(id)
}

