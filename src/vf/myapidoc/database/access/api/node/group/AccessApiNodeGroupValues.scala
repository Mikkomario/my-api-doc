package vf.myapidoc.database.access.api.node.group

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel

/**
  * Used for accessing api node group values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroupValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node group database properties
	  */
	override val model = ApiNodeGroupDbModel
	
	/**
	  * ID of the API this node group belongs to
	  */
	lazy val apiIds = apply(model.apiId) { v => v.getInt }
	/**
	  * (Human readable) name of this node group
	  */
	lazy val names = apply(model.name) { v => v.getString }
	/**
	  * Description of this group's function or theme
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
}

