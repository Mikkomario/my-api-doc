package vf.myapidoc.database.access.api.update

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.value.DocUpdateId

/**
  * Used for accessing individual versioned doc element values from the DB
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait AccessVersionedDocElementValue extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Access to versioned doc element id
	  */
	lazy val id = apply(model.index).optional { _.int }
	/**
	  * ID of the update that introduced this element (version)
	  */
	lazy val originalUpdateId = apply(model.originalUpdateId).optional { v => v.int }
	/**
	  * ID of the update that removed or replaced this element (version)
	  */
	lazy val removingUpdateId = apply(model.removingUpdateId).optional { v => v.int }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing versioned doc element database properties
	  */
	def model: VersionedDocElementDbProps
	
	
	// OTHER    --------------------
	
	/**
	 * Deprecates the accessible item
	 * @param connection Implicit DB connection
	 * @param update Update that introduces this change
	 * @return Whether any row was targeted
	 */
	def deprecate()(implicit connection: Connection, update: DocUpdateId) =
		removingUpdateId.set(update.connectedValue)
}

