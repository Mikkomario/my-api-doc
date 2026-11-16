package vf.myapidoc.model.stored.api

import vf.myapidoc.database.access.api.params.path.AccessPathParametersSchemaLink
import vf.myapidoc.model.factory.api.PathParametersSchemaLinkFactoryWrapper
import vf.myapidoc.model.partial.api.{PathParametersSchemaLinkData, VersionedDocElementData}

/**
  * Represents a path parameters schema link that has already been stored in the database. 
  * Applies a specific schema to the path parameters received by an API node.
  * @tparam Repr The implementing type
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
@deprecated("Replaced with ApiNodePathLike", "v0.1")
trait PathParametersSchemaLinkLike[+Repr <: VersionedDocElementData] 
	extends PathParametersSchemaLinkFactoryWrapper[PathParametersSchemaLinkData, Repr] 
		with VersionedDocElementData with StoredVersionedDocElementLike[PathParametersSchemaLinkData, Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this path parameters schema link in the database
	  */
	def access = AccessPathParametersSchemaLink(id)
}

