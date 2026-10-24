package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationModelSchemaLinkData
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementationModelSchemaLink

import java.time.Instant

object ApiNodeMethodImplementationModelSchemaLinkDbReader
{
	// OTHER	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing api node method implementation model schema links from database 
	  * model data
	  */
	def apply(table: Table, 
		dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps): ApiNodeMethodImplementationModelSchemaLinkDbReader = 
		_ApiNodeMethodImplementationModelSchemaLinkDbReader(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table   Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _ApiNodeMethodImplementationModelSchemaLinkDbReader(table: Table, 
		dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps) 
		extends ApiNodeMethodImplementationModelSchemaLinkDbReader
	{
		// IMPLEMENTED	--------------------
		
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
			ApiNodeMethodImplementationModelSchemaLink(id, 
				ApiNodeMethodImplementationModelSchemaLinkData(implementationId, modelTypeId, created, 
				deprecatedAfter))
	}
}

/**
  * Common trait for factories which parse api node method implementation model schema link data 
  * from database-originated models
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbReader 
	extends ApiNodeMethodImplementationModelSchemaLinkDbReaderLike[ApiNodeMethodImplementationModelSchemaLink]

