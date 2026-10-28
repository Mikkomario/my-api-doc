package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.DocUpdateDbModel
import vf.myapidoc.model.partial.api.DocUpdateData
import vf.myapidoc.model.stored.api.DocUpdate

import scala.util.Success

/**
  * Used for reading doc update data from the DB
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
object DocUpdateDbReader extends DbRowReader[DocUpdate] with ParseTableModel[DocUpdate] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = DocUpdateDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(DocUpdate(valid(this.model.id.name).getInt, 
			DocUpdateData(apiId = valid(this.model.apiId.name).getInt, 
			created = valid(this.model.created.name).getInstant)))
}

