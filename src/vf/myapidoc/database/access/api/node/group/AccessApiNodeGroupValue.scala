package vf.myapidoc.database.access.api.node.group

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel

/**
  * Used for accessing individual api node group values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroupValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node group database properties
	  */
	val model = ApiNodeGroupDbModel
	
	/**
	  * Access to api node group id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
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
	
	/**
	  * Time when this api node group was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this api node group became deprecated. None while this api node group is still 
	  * valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
}

