package vf.myapidoc.database.access.api.node

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValue
import vf.myapidoc.database.storable.api.ApiNodeDbModel

/**
  * Used for accessing individual api node values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessApiNodeValue(access: AccessColumn) extends AccessVersionedDocElementValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing api node database properties
	  */
	override val model = ApiNodeDbModel
	
	/**
	  * ID of the node group this node belongs to
	  */
	lazy val groupId = apply(model.groupId).optional { v => v.int }
	/**
	  * Human-readable name of this node
	  */
	lazy val name = apply(model.name) { v => v.getString }
	/**
	  * A lowercase version of the node name, with no whitespace included. Used for indexing & search.
	  */
	lazy val nameNoSpaces = apply(model.nameNoSpaces) { v => v.getString }
	/**
	  * Path of this node, relative to the parent node (if defined) or the API root (if no parent is 
	  * defined). 
	  * May contain path parameters.
	  */
	lazy val relativePath = apply(model.relativePath) { v => v.getString }
	/**
	  * ID of the node that appears directly above/before this node on the path. 
	  * None if no parent node appears between this node and the API root.
	  */
	lazy val parentId = apply(model.parentId).optional { v => v.int }
	/**
	  * A description of this API node.
	  */
	lazy val description = apply(model.description) { v => v.getString }
}

