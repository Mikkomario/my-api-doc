package vf.myapidoc.database.access.api.response.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.targeting.columns.{AccessManyColumns, AccessValues}
import vf.myapidoc.database.storable.api.ResponseContentTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType

/**
  * Used for accessing response content type values from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessResponseContentTypeValues(access: AccessManyColumns) extends AccessValues
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Interface for accessing response content type database properties
	  */
	val model = ResponseContentTypeDbModel
	
	/**
	  * Access to response content type ids
	  */
	lazy val ids = apply(model.index) { _.getInt }
	
	/**
	  * ID of the status this type of response body is returned with.
	  */
	lazy val statusIds = apply(model.statusId) { v => v.getInt }
	
	/**
	  * If this body is non-empty, specifies the type of content it contains.
	  */
	lazy val contentTypes = apply(model.contentType).flatten { v => PropertyType.findForValue(v) }
	
	/**
	  * If this body contains a JSON object or objects, contains the ID of the model schema that 
	  * describes the applicable object structure.
	  */
	lazy val modelTypeIds = apply(model.modelTypeId).flatten { v => v.int }
	
	/**
	  * Time when this response content type was added to the database
	  */
	lazy val creationTimes = apply(model.created) { v => v.getInstant }
	
	/**
	  * Time when this response content type became deprecated. None while this response content type 
	  * is still valid.
	  */
	lazy val deprecationTimes = apply(model.deprecatedAfter).flatten { v => v.instant }
	
	/**
	  * Whether this response body contains an array of values. False if no more than one value may 
	  * be returned.
	  */
	lazy val areArrays = apply(model.isArray) { v => v.getBoolean }
	
	/**
	  * Whether this response body is empty / no response body is returned.
	  */
	lazy val areEmpty = apply(model.isEmpty) { v => v.getBoolean }
}

