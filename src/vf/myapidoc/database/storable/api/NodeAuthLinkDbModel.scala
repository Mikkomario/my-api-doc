package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.NodeAuthLinkDbProps

object NodeAuthLinkDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param table The primarily targeted table
	  * @param props Targeted database properties
	  * @return A factory used for constructing node auth link models using the specified configuration
	  */
	def factory(table: Table, props: NodeAuthLinkDbProps) = NodeAuthLinkDbModelFactory(table, props)
}

/**
  * Common trait for database interaction models dealing with node auth links
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbModel extends VersionedDocElementDbModel with NodeAuthLinkDbModelLike[NodeAuthLinkDbModel]

