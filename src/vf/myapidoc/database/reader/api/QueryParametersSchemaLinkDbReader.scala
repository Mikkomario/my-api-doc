package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel
import vf.myapidoc.model.partial.api.QueryParametersSchemaLinkData
import vf.myapidoc.model.stored.api.QueryParametersSchemaLink

import java.time.Instant

/**
  * Used for reading query parameters schema link data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object QueryParametersSchemaLinkDbReader 
	extends ApiNodeMethodImplementationModelSchemaLinkDbReaderLike[QueryParametersSchemaLink]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = QueryParametersSchemaLinkDbModel
	
	
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
		QueryParametersSchemaLink(id, QueryParametersSchemaLinkData(implementationId = implementationId, 
			modelTypeId = modelTypeId, created = created, deprecatedAfter = deprecatedAfter))
}

