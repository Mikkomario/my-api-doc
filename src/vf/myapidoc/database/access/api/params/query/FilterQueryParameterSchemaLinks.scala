package vf.myapidoc.database.access.api.params.query

import vf.myapidoc.database.access.api.node.impl.link.model.FilterApiNodeMethodImplementationModelSchemaLinks
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel

/**
  * Common trait for access points which may be filtered based on query parameters schema link 
  * properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterQueryParameterSchemaLinks[+Repr] extends FilterApiNodeMethodImplementationModelSchemaLinks[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines query parameters schema link database properties
	  */
	def model = QueryParametersSchemaLinkDbModel
}

