package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.ApiDbModel
import vf.myapidoc.model.partial.api.ApiData
import vf.myapidoc.model.stored.api.Api

import scala.util.Success

/**
  * Used for reading api data from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
object ApiDbReader extends DbRowReader[Api] with ParseTableModel[Api] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ApiDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(Api(valid(this.model.id.name).getInt, ApiData(name = valid(this.model.name.name).getString, 
			created = valid(this.model.created.name).getInstant)))
}

