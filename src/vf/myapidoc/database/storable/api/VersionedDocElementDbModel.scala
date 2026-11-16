package vf.myapidoc.database.storable.api

import vf.myapidoc.database.props.api.VersionedDocElementDbProps

object VersionedDocElementDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param props Targeted database properties
	  * @return A factory used for constructing versioned doc element models using the specified configuration
	  */
	def factory(props: VersionedDocElementDbProps) = VersionedDocElementDbModelFactory(props)
}

/**
  * Common trait for database interaction models dealing with versioned doc elements
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait VersionedDocElementDbModel extends VersionedDocElementDbModelLike[VersionedDocElementDbModel]

