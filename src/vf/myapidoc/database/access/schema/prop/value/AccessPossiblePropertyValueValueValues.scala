package vf.myapidoc.database.access.schema.prop.value

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.access.api.update.AccessVersionedDocElementValues
import vf.myapidoc.database.storable.schema.PossiblePropertyValueDbModel

/**
  * Used for accessing possible property value values from the DB
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
case class AccessPossiblePropertyValueValueValues(access: AccessManyColumns) extends AccessVersionedDocElementValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing possible property value database properties
	  */
	override val model = PossiblePropertyValueDbModel
	
	/**
	  * ID of the property that this value may be assigned to
	  */
	lazy val propIds = apply(model.propId) { v => v.getInt }
	/**
	  * The value the linked property may have
	  */
	lazy val values = apply(model.value) { v => v.getString }
	/**
	  * Description of this value's role / function
	  */
	lazy val descriptions = apply(model.description) { v => v.getString }
}

