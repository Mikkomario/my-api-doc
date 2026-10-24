package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.util.UncertainBoolean
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.PossibleResponseStatusDbModel
import vf.myapidoc.model.partial.api.PossibleResponseStatusData
import vf.myapidoc.model.stored.api.PossibleResponseStatus

import scala.util.Success

/**
  * Used for reading possible response status data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object PossibleResponseStatusDbReader 
	extends DbRowReader[PossibleResponseStatus] with ParseTableModel[PossibleResponseStatus] 
		with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = PossibleResponseStatusDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(PossibleResponseStatus(valid(this.model.id.name).getInt, 
			PossibleResponseStatusData(implementationId = valid(this.model.implementationId.name).getInt, 
			statusCode = valid(this.model.statusCode.name).getInt, 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant, 
			hasBody = UncertainBoolean(valid(this.model.hasBody.name).boolean))))
}

