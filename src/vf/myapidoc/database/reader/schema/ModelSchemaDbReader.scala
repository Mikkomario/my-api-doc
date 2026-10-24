package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.ModelSchema

import scala.util.Success

/**
  * Used for reading model schema data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ModelSchemaDbReader 
	extends DbRowReader[ModelSchema] with ParseTableModel[ModelSchema] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ModelSchemaDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ModelSchema(valid(this.model.id.name).getInt, 
			ModelSchemaData(apiId = valid(this.model.apiId.name).getInt, 
			name = valid(this.model.name.name).getString, 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

