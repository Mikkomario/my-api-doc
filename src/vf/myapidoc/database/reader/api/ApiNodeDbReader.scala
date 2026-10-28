package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
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
object ApiNodeDbReader extends VersionedDocElementDbReaderLike[ApiNode]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ApiNodeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		ApiNode(id, ApiNodeData(originalUpdateId = originalUpdateId, 
			groupId = model(dbProps.groupId.name).getInt, name = model(dbProps.name.name).getString, 
			nameNoSpaces = model(dbProps.nameNoSpaces.name).getString, 
			relativePath = model(dbProps.relativePath.name).getString, removingUpdateId = removingUpdateId, 
			parentId = model(dbProps.parentId.name).int, 
			description = model(dbProps.description.name).getString))
}

