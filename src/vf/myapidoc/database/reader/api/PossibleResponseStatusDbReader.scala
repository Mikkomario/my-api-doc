package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
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
object PossibleResponseStatusDbReader extends VersionedDocElementDbReaderLike[PossibleResponseStatus]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = PossibleResponseStatusDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		PossibleResponseStatus(id, PossibleResponseStatusData(originalUpdateId = originalUpdateId, 
			implementationId = model(dbProps.implementationId.name).getInt, 
			statusCode = model(dbProps.statusCode.name).getInt, removingUpdateId = removingUpdateId, 
			description = model(dbProps.description.name).getString, 
			hasBody = UncertainBoolean(model(dbProps.hasBody.name).boolean)))
}

