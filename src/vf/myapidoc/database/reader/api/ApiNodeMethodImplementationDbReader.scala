package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.ApiNodeMethodImplementationDbModel
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationData
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementation

import scala.util.Success

/**
  * Used for reading api node method implementation data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object ApiNodeMethodImplementationDbReader 
	extends DbRowReader[ApiNodeMethodImplementation] with ParseTableModel[ApiNodeMethodImplementation] 
		with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ApiNodeMethodImplementationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ApiNodeMethodImplementation(valid(this.model.id.name).getInt, 
			ApiNodeMethodImplementationData(nodeId = valid(this.model.nodeId.name).getInt, 
			method = Method(valid(this.model.method.name).getString), 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant, 
			requiresRequestBody = valid(this.model.requiresRequestBody.name).getBoolean)))
}

