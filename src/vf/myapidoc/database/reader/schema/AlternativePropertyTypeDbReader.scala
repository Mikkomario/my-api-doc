package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.schema.AlternativePropertyTypeData
import vf.myapidoc.model.stored.schema.AlternativePropertyType

import scala.util.Success

/**
  * Used for reading alternative property type data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AlternativePropertyTypeDbReader 
	extends DbRowReader[AlternativePropertyType] with ParseTableModel[AlternativePropertyType] 
		with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = AlternativePropertyTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) =
		Success(AlternativePropertyType(valid(this.model.id.name).getInt,
			AlternativePropertyTypeData(
				propId = valid(this.model.propId.name).getInt,
				altType = PropertyType.fromValue(valid(this.model.altType.name)),
				created = valid(this.model.created.name).getInstant,
				deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

