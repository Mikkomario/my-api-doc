package vf.myapidoc.database.access.api.body

import vf.myapidoc.database.access.api.node.impl.link.model.FilterApiNodeMethodImplementationModelSchemaLinks
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel

/**
  * Common trait for access points which may be filtered based on request body schema properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterRequestBodySchemas[+Repr] extends FilterApiNodeMethodImplementationModelSchemaLinks[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines request body schema database properties
	  */
	def model = RequestBodySchemaDbModel
}

