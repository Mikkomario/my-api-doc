package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.ApiNodeGroupDbModel
import vf.myapidoc.model.partial.api.ApiNodeGroupData
import vf.myapidoc.model.stored.api.ApiNodeGroup

import scala.util.Success

/**
  * Used for reading api node group data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ApiNodeGroupDbReader 
	extends DbRowReader[ApiNodeGroup] with ParseTableModel[ApiNodeGroup] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ApiNodeGroupDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ApiNodeGroup(valid(this.model.id.name).getInt, 
			ApiNodeGroupData(apiId = valid(this.model.apiId.name).getInt, 
			name = valid(this.model.name.name).getString, 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

