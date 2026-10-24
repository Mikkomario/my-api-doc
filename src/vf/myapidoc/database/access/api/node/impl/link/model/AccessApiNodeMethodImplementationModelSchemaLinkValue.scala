package vf.myapidoc.database.access.api.node.impl.link.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

/**
  * Used for accessing individual api node method implementation model schema link values from 
  * the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait AccessApiNodeMethodImplementationModelSchemaLinkValue extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Access to api node method implementation model schema link id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the method implementation that uses the linked schema.
	  */
	lazy val implementationId = apply(model.implementationId).optional { v => v.int }
	
	/**
	  * ID of the schema that defines the type of model accepted by the linked implementation.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	
	/**
	  * Time when this api node method implementation model schema link was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this api node method implementation model schema link became deprecated. None while 
	  * this api node method implementation model schema link is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
	
	
	// ABSTRACT	--------------------
	
	/**
	  * Interface for accessing api node method implementation model schema link database properties
	  */
	def model: ApiNodeMethodImplementationModelSchemaLinkDbProps
}

