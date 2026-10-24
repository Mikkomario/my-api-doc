package vf.myapidoc.database.access.api.node.auth.scope

import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.node.auth.AccessNodeAuthLinkValues
import vf.myapidoc.database.storable.api.NodeAuthScopeLinkDbModel

/**
  * Used for accessing node auth scope link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthScopeLinkValues(access: AccessManyColumns) extends AccessNodeAuthLinkValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing node auth scope link database properties
	  */
	override val model = NodeAuthScopeLinkDbModel
}

