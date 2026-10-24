package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.RequestBodySchema

import java.time.Instant

/**
  * Used for reading request body schema data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object RequestBodySchemaDbReader 
	extends ApiNodeMethodImplementationModelSchemaLinkDbReaderLike[RequestBodySchema]
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
	  * @param id               Id to assign to the read/parsed api node method implementation model 
	  *                         schema link
	  * @param implementationId implementation id to assign to the new api node method implementation 
	  *                         model schema link
	  * @param modelTypeId      model type id to assign to the new api node method implementation 
	  *                         model schema link
	  * @param created          created to assign to the new api node method implementation model 
	  *                         schema link
	  * @param deprecatedAfter  deprecated after to assign to the new api node method implementation 
	  *                         model schema link
	  */
	override protected def apply(model: HasProperties, id: Int, implementationId: Int, modelTypeId: Int, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		RequestBodySchema(id, RequestBodySchemaData(implementationId = implementationId, 
			modelTypeId = modelTypeId, created = created, deprecatedAfter = deprecatedAfter, 
			customContentType = model(dbProps.customContentType.name).getString, 
			description = model(dbProps.description.name).getString, 
			isArray = model(dbProps.isArray.name).getBoolean))
}

