package vf.myapidoc.model.stored.schema

import vf.myapidoc.database.access.schema.prop.AccessPropertySchema
import vf.myapidoc.model.factory.schema.PropertySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.VersionedDocElementData
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.api.StoredVersionedDocElementLike

/**
  * Represents a property schema that has already been stored in the database. 
  * Defines an individual property in a model schema.
  * @tparam Repr The implementing type
  * @author Mikko Hilpinen
  * @since 10.11.2026, v0.1
  */
trait StoredPropertySchemaLike[+Repr <: VersionedDocElementData] 
	extends PropertySchemaFactoryWrapper[PropertySchemaData, Repr] with VersionedDocElementData 
		with StoredVersionedDocElementLike[PropertySchemaData, Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this property schema in the database
	  */
	def access = AccessPropertySchema(id)
}

