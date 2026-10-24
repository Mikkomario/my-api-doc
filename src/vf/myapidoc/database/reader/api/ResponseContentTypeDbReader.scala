package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.ResponseContentTypeDbModel
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.ResponseContentTypeData
import vf.myapidoc.model.stored.api.ResponseContentType

import scala.util.Success

/**
  * Used for reading response content type data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ResponseContentTypeDbReader 
	extends DbRowReader[ResponseContentType] with ParseTableModel[ResponseContentType] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ResponseContentTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ResponseContentType(valid(this.model.id.name).getInt, 
			ResponseContentTypeData(statusId = valid(this.model.statusId.name).getInt, 
			contentType = PropertyType.findForValue(valid(this.model.contentType.name)), 
			modelTypeId = valid(this.model.modelTypeId.name).int, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant, 
			isArray = valid(this.model.isArray.name).getBoolean, 
			isEmpty = valid(this.model.isEmpty.name).getBoolean)))
}

