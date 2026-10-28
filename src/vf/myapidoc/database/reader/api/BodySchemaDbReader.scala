package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.BodySchemaData
import vf.myapidoc.model.stored.api.BodySchema

object BodySchemaDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing body schemas from database model data
	  */
	def apply(dbProps: BodySchemaDbProps): BodySchemaDbReader = _BodySchemaDbReader(dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _BodySchemaDbReader(dbProps: BodySchemaDbProps) extends BodySchemaDbReader
	{
		// IMPLEMENTED	--------------------
		
		override def table = dbProps.table
		
		/**
		  * @param model            Model from which additional data may be read
		  * @param id               ID to assign to the read/parsed body schema
		  * @param originalUpdateId original update id to assign to the new body schema
		  * @param parentId         parent id to assign to the new body schema
		  * @param removingUpdateId removing update id to assign to the new body schema
		  * @param dataType         data type to assign to the new body schema
		  * @param modelTypeId      model type id to assign to the new body schema
		  * @param isArray          is array to assign to the new body schema
		  */
		override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, parentId: Int, 
			removingUpdateId: Option[Int], dataType: PropertyType, modelTypeId: Option[Int], 
			isArray: Boolean) = 
			BodySchema(id, BodySchemaData(originalUpdateId, parentId, removingUpdateId, dataType, 
				modelTypeId, isArray))
	}
}

/**
  * Common trait for factories that parse body schema data from database-originated models
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbReader extends BodySchemaDbReaderLike[BodySchema]

