package vf.myapidoc.database.access.api.params.query

import utopia.vault.nosql.targeting.columns.AccessManyColumns
import vf.myapidoc.database.access.api.node.impl.link.model.AccessApiNodeMethodImplementationModelSchemaLinkValues
import vf.myapidoc.database.storable.api.QueryParametersSchemaLinkDbModel

/**
  * Used for accessing query parameters schema link values from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessQueryParametersSchemaLinkValues(access: AccessManyColumns) 
	extends AccessApiNodeMethodImplementationModelSchemaLinkValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing query parameters schema link database properties
	  */
	override val model = QueryParametersSchemaLinkDbModel
}

