package vf.myapidoc.database.access.api.response.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.AccessColumns.AccessColumn
import utopia.vault.nosql.targeting.columns.AccessValue
import vf.myapidoc.database.storable.api.ResponseContentTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing individual response content type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessResponseContentTypeValue(access: AccessColumn) extends AccessValue
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing response content type database properties
	  */
	val model = ResponseContentTypeDbModel
	
	/**
	  * Access to response content type id
	  */
	lazy val id = apply(model.index).optional { _.int }
	
	/**
	  * ID of the status this type of response body is returned with.
	  */
	lazy val statusId = apply(model.statusId).optional { v => v.int }
	
	/**
	  * If this body is non-empty, specifies the type of content it contains.
	  */
	lazy val contentType = apply(model.contentType).optional { v => PropertyType.findForValue(v) }
	
	/**
	  * If this body contains a JSON object or objects, contains the ID of the model schema that 
	  * describes the applicable object structure.
	  */
	lazy val modelTypeId = apply(model.modelTypeId).optional { v => v.int }
	
	/**
	  * Time when this response content type was added to the database
	  */
	lazy val created = apply(model.created).optional { v => v.instant }
	
	/**
	  * Time when this response content type became deprecated. None while this response content type 
	  * is still valid.
	  */
	lazy val deprecatedAfter = apply(model.deprecatedAfter).optional { v => v.instant }
	
	/**
	  * Whether this response body contains an array of values. False if no more than one value may 
	  * be returned.
	  */
	lazy val isArray = apply(model.isArray).optional { v => v.boolean }
	
	/**
	  * Whether this response body is empty / no response body is returned.
	  */
	lazy val isEmpty = apply(model.isEmpty).optional { v => v.boolean }
}

