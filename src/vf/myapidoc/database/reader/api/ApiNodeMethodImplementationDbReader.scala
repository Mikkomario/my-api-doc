package vf.myapidoc.database.reader.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
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
object ApiNodeMethodImplementationDbReader extends VersionedDocElementDbReaderLike[ApiNodeMethodImplementation]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ApiNodeMethodImplementationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		ApiNodeMethodImplementation(id, ApiNodeMethodImplementationData(originalUpdateId = originalUpdateId, 
			nodeId = model(dbProps.nodeId.name).getInt, removingUpdateId = removingUpdateId, 
			method = Method(model(dbProps.method.name).getString), 
			description = model(dbProps.description.name).getString, 
			requiresRequestBody = model(dbProps.requiresRequestBody.name).getBoolean))
}

