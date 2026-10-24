package vf.myapidoc.database.storable.api

import utopia.vault.model.immutable.Table
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

object ApiNodeMethodImplementationModelSchemaLinkDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param table The primarily targeted table
	  * @param props Targeted database properties
	  * @return A factory used for constructing api node method implementation model schema link models using 
	  * the specified configuration
	  */
	def factory(table: Table, props: ApiNodeMethodImplementationModelSchemaLinkDbProps) = 
		ApiNodeMethodImplementationModelSchemaLinkDbModelFactory(table, props)
}

/**
  * Common trait for database interaction models dealing with api node method implementation 
  * model schema links
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbModel 
	extends ApiNodeMethodImplementationModelSchemaLinkDbModelLike[ApiNodeMethodImplementationModelSchemaLinkDbModel]

