package vf.myapidoc.database.access.api.params.query

import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import vf.myapidoc.database.access.api.node.impl.link.model.AccessApiNodeMethodImplementationModelSchemaLinkValue
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel

/**
  * Used for accessing individual query parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessQueryParametersSchemaLinkValue(access: AccessColumn) 
	extends AccessApiNodeMethodImplementationModelSchemaLinkValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing query parameters schema link database properties
	  */
	override val model = QueryParametersSchemaLinkDbModel
}

