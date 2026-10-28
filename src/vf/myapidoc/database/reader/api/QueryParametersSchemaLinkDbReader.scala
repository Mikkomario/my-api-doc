package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel
import vf.myapidoc.model.partial.api.QueryParametersSchemaLinkData
import vf.myapidoc.model.stored.api.QueryParametersSchemaLink

/**
  * Used for reading query parameters schema link data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object QueryParametersSchemaLinkDbReader extends VersionedDocElementDbReaderLike[QueryParametersSchemaLink]
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
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		QueryParametersSchemaLink(id, QueryParametersSchemaLinkData(originalUpdateId = originalUpdateId, 
			implementationId = model(dbProps.implementationId.name).getInt, 
			modelTypeId = model(dbProps.modelTypeId.name).getInt, removingUpdateId = removingUpdateId))
}

