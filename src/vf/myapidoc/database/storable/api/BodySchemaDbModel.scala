package vf.myapidoc.database.storable.api

import vf.myapidoc.database.props.api.BodySchemaDbProps

object BodySchemaDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param props Targeted database properties
	  * @return A factory used for constructing body schema models using the specified configuration
	  */
	def factory(props: BodySchemaDbProps) = BodySchemaDbModelFactory(props)
}

/**
  * Common trait for database interaction models dealing with body schemas
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbModel extends VersionedDocElementDbModel with BodySchemaDbModelLike[BodySchemaDbModel]

