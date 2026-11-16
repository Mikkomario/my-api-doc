package vf.myapidoc.database.storable.api

import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

object NodeAuthLinkDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param props Targeted database properties
	  * @return A factory used for constructing node auth link models using the specified configuration
	  */
	def factory(props: NodeAuthLinkDbProps) = NodeAuthLinkDbModelFactory(props)
}

/**
  * Common trait for database interaction models dealing with node auth links
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbModel extends VersionedDocElementDbModel with NodeAuthLinkDbModelLike[NodeAuthLinkDbModel]

