package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.storable.api.NodeAuthTypeLinkDbModel

/**
  * Used for accessing individual node auth type link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthTypeLinkValue(access: AccessColumn) extends AccessNodeAuthLinkValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing node auth type link database properties
	  */
	override val model = NodeAuthTypeLinkDbModel
}

