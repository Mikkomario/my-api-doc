package vf.myapidoc.database.access.api.node.group

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel

/**
  * Used for accessing individual api node group values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroupValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node group database properties
	  */
	override val model = ApiNodeGroupDbModel
	
	/**
	  * ID of the API this node group belongs to
	  */
	lazy val apiId = apply(model.apiId).optional { v => v.int }
	/**
	  * (Human readable) name of this node group
	  */
	lazy val name = apply(model.name) { v => v.getString }
	/**
	  * Description of this group's function or theme
	  */
	lazy val description = apply(model.description) { v => v.getString }
}

