package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.BodySchemaData
import vf.myapidoc.model.stored.api.BodySchema

import java.time.Instant

object BodySchemaDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing body schemas from database model data
	  */
	def apply(table: Table, dbProps: BodySchemaDbProps): BodySchemaDbReader = _BodySchemaDbReader(table, 
		dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _BodySchemaDbReader(table: Table, 
		dbProps: BodySchemaDbProps) extends BodySchemaDbReader
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param model           Model from which additional data may be read
		  * @param id              Id to assign to the read/parsed body schema
		  * @param parentId        parent id to assign to the new body schema
		  * @param dataType        data type to assign to the new body schema
		  * @param modelTypeId     model type id to assign to the new body schema
		  * @param created         created to assign to the new body schema
		  * @param deprecatedAfter deprecated after to assign to the new body schema
		  * @param isArray         is array to assign to the new body schema
		  */
		override protected def apply(model: HasProperties, id: Int, parentId: Int, dataType: PropertyType, 
			modelTypeId: Option[Int], created: Instant, deprecatedAfter: Option[Instant], isArray: Boolean) = 
			BodySchema(id, BodySchemaData(parentId, dataType, modelTypeId, created, deprecatedAfter, isArray))
	}
}

/**
  * Common trait for factories which parse body schema data from database-originated models
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbReader extends BodySchemaDbReaderLike[BodySchema]

