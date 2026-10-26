package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.schema.PossiblePropertyValueDbModel
import vf.myapidoc.model.partial.schema.PossiblePropertyValueData
import vf.myapidoc.model.stored.schema.PossiblePropertyValue

import scala.util.Success

/**
  * Used for reading possible property value data from the DB
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
object PossiblePropertyValueDbReader 
	extends DbRowReader[PossiblePropertyValue] with ParseTableModel[PossiblePropertyValue] 
		with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = PossiblePropertyValueDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(PossiblePropertyValue(valid(this.model.id.name).getInt, 
			PossiblePropertyValueData(propId = valid(this.model.propId.name).getInt, 
			value = valid(this.model.value.name).getString, 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

