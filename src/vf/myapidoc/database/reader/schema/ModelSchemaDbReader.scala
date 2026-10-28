package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
import vf.myapidoc.database.storable.schema.ModelSchemaDbModel
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.ModelSchema

import scala.util.Success

/**
  * Used for reading model schema data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ModelSchemaDbReader extends VersionedDocElementDbReaderLike[ModelSchema]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ModelSchemaDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		ModelSchema(id, ModelSchemaData(originalUpdateId = originalUpdateId, 
			apiId = model(dbProps.apiId.name).getInt, removingUpdateId = removingUpdateId, 
			name = model(dbProps.name.name).getString, 
			description = model(dbProps.description.name).getString))
}

