package vf.myapidoc.database.access.api.node.auth

import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.storable.api.NodeAuthTypeLinkDbModel

/**
  * Used for accessing node auth type link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessNodeAuthTypeLinkValues(access: AccessManyColumns) extends AccessNodeAuthLinkValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing node auth type link database properties
	  */
	override val model = NodeAuthTypeLinkDbModel
}

