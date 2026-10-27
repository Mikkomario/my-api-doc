package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.schema.PropertySchemaDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.schema.StoredPropertySchema

import scala.util.Success

/**
  * Used for reading property schema data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object PropertySchemaDbReader 
	extends DbRowReader[StoredPropertySchema] with ParseTableModel[StoredPropertySchema] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = PropertySchemaDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = Success(StoredPropertySchema(valid(this.model.id.name).getInt,
		PropertySchemaData(modelTypeId = valid(this.model.modelTypeId.name).getInt,
			name = valid(this.model.name.name).getString,
			dataType = PropertyType.fromValue(valid(this.model.dataType.name)),
			contentTypeId = valid(this.model.contentTypeId.name).int,
			description = valid(this.model.description.name).getString,
			created = valid(this.model.created.name).getInstant,
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant,
			isArray = valid(this.model.isArray.name).boolean,
			isOptional = valid(this.model.isOptional.name).getBoolean)))
}

