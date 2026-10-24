package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.ApiNodeDbModel
import vf.myapidoc.model.partial.api.ApiNodeData
import vf.myapidoc.model.stored.api.ApiNode

import scala.util.Success

/**
  * Used for reading api node data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ApiNodeDbReader extends DbRowReader[ApiNode] with ParseTableModel[ApiNode] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ApiNodeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(ApiNode(valid(this.model.id.name).getInt, 
			ApiNodeData(groupId = valid(this.model.groupId.name).getInt, 
			name = valid(this.model.name.name).getString, 
			nameNoSpaces = valid(this.model.nameNoSpaces.name).getString, 
			relativePath = valid(this.model.relativePath.name).getString, 
			parentId = valid(this.model.parentId.name).int, 
			description = valid(this.model.description.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

