package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.RequestBodySchema

/**
  * Used for reading request body schema data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object RequestBodySchemaDbReader extends BodySchemaDbReaderLike[RequestBodySchema]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = RequestBodySchemaDbModel
	
	
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
		RequestBodySchema(id, RequestBodySchemaData(originalUpdateId = originalUpdateId, 
			implementationId = parentId, removingUpdateId = removingUpdateId, dataType = dataType, 
			modelTypeId = modelTypeId, isArray = isArray, 
			customContentType = model(dbProps.customContentType.name).getString, 
			description = model(dbProps.description.name).getString))
}

