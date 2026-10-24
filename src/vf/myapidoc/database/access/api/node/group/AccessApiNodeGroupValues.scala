package vf.myapidoc.database.access.api.node.group

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel

/**
  * Used for accessing api node group values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeGroupValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node group database properties
	  */
	val model = ApiNodeGroupDbModel
	
	/**
	  * Access to api node group ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
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
	
	/**
	  * Time when this api node group was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	
	/**
	  * Time when this api node group became deprecated. None while this api node group is still 
	  * valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
}

