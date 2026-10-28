package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.VersionedDocElementDbProps

object VersionedDocElementDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param table The primarily targeted table
	  * @param props Targeted database properties
	  * @return A factory used for constructing versioned doc element models using the specified configuration
	  */
	def factory(table: Table, props: VersionedDocElementDbProps) = VersionedDocElementDbModelFactory(table, 
		props)
}

/**
  * Common trait for database interaction models dealing with versioned doc elements
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbModel extends VersionedDocElementDbModelLike[VersionedDocElementDbModel]

