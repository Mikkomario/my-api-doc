package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.ResponseBodySchemaDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.ResponseBodySchemaData
import vf.myapidoc.model.stored.api.ResponseBodySchema

/**
  * Used for reading response body schema data from the DB
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
object ResponseBodySchemaDbReader extends BodySchemaDbReaderLike[ResponseBodySchema]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ResponseBodySchemaDbModel
	
	
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
		ResponseBodySchema(id, ResponseBodySchemaData(originalUpdateId = originalUpdateId, 
			statusId = parentId, removingUpdateId = removingUpdateId, dataType = dataType, 
			modelTypeId = modelTypeId, isArray = isArray))
}

