package vf.myapidoc.database.reader.schema

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.flow.util.UncertainBoolean
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
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
object PropertySchemaDbReader extends VersionedDocElementDbReaderLike[StoredPropertySchema]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = PropertySchemaDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		StoredPropertySchema(id, PropertySchemaData(originalUpdateId = originalUpdateId, 
			modelTypeId = model(dbProps.modelTypeId.name).getInt, removingUpdateId = removingUpdateId, 
			name = model(dbProps.name.name).getString, 
			dataType = PropertyType.fromValue(model(dbProps.dataType.name)), 
			contentTypeId = model(dbProps.contentTypeId.name).int, 
			description = model(dbProps.description.name).getString, 
			isArray = UncertainBoolean(model(dbProps.isArray.name).boolean), 
			isOptional = model(dbProps.isOptional.name).getBoolean))
}

