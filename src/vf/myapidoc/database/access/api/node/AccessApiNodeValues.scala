package vf.myapidoc.database.access.api.node

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.ApiNodeDbModel

/**
  * Used for accessing api node values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node database properties
	  */
	val model = ApiNodeDbModel
	
	/**
	  * Access to api node ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * ID of the node group this node belongs to
	  */
	lazy val groupIds = apply(model.groupId) { v => v.getInt }
	
	/**
	  * Human-readable name of this node
	  */
	lazy val names = apply(model.name) { v => v.getString }
	
	/**
	  * A lowercase version of the node name, with no whitespace included. Used for indexing & search.
	  */
	lazy val namesNoSpaces = apply(model.nameNoSpaces) { v => v.getString }
	
	/**
	  * Path of this node, relative to the parent node (if defined) or the API root (if no parent is 
	  * defined). 
	  * May contain path parameters.
	  */
	lazy val relativePaths = apply(model.relativePath) { v => v.getString }
	
	/**
	  * ID of the node that appears directly above/before this node on the path. 
	  * None if no parent node appears between this node and the API root.
	  */
	lazy val parentIds = apply(model.parentId).flatten { v => v.int }
	
	/**
	  * A description of this API node.
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
	
	/**
	  * Time when this api node was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	
	/**
	  * Time when this api node became deprecated. None while this api node is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
}

