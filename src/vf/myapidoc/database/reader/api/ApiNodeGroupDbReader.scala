package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
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
object ApiNodeGroupDbReader extends VersionedDocElementDbReaderLike[ApiNodeGroup]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ApiNodeGroupDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		ApiNodeGroup(id, ApiNodeGroupData(originalUpdateId = originalUpdateId, 
			apiId = model(dbProps.apiId.name).getInt, name = model(dbProps.name.name).getString, 
			removingUpdateId = removingUpdateId, description = model(dbProps.description.name).getString))
}

