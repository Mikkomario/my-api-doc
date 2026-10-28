package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.AccessValues
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.value.DocUpdateId

/**
  * Used for accessing versioned doc element values from the DB
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait AccessVersionedDocElementValues extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Access to versioned doc element ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	/**
	  * ID of the update that introduced this element (version)
	  */
	lazy val originalUpdateIds = apply(model.originalUpdateId) { v => v.getInt }
	/**
	  * ID of the update that removed or replaced this element (version)
	  */
	lazy val removingUpdateIds = apply(model.removingUpdateId).flatten { v => v.int }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing versioned doc element database properties
	  */
	def model: VersionedDocElementDbProps
	
	
	// OTHER    --------------------
	
	/**
	 * Deprecates all accessible elements
	 * @param connection Implicit DB connection
	 * @param updateId Implicit doc update ID
	 * @return Whether any row was targeted
	 */
	def deprecate()(implicit connection: Connection, updateId: DocUpdateId) =
		removingUpdateIds.set(updateId.connectedValue)
}

