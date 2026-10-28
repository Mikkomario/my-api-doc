package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.BodySchemaDbProps

object BodySchemaDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param table The primarily targeted table
	  * @param props Targeted database properties
	  * @return A factory used for constructing body schema models using the specified configuration
	  */
	def factory(table: Table, props: BodySchemaDbProps) = BodySchemaDbModelFactory(table, props)
}

/**
  * Common trait for database interaction models dealing with body schemas
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbModel extends VersionedDocElementDbModel with BodySchemaDbModelLike[BodySchemaDbModel]

