package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.PathParametersSchemaLinkDbModel
import vf.myapidoc.model.partial.api.PathParametersSchemaLinkData
import vf.myapidoc.model.stored.api.PathParametersSchemaLink

import scala.util.Success

/**
  * Used for reading path parameters schema link data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object PathParametersSchemaLinkDbReader extends VersionedDocElementDbReaderLike[PathParametersSchemaLink]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = PathParametersSchemaLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		PathParametersSchemaLink(id, PathParametersSchemaLinkData(originalUpdateId = originalUpdateId, 
			nodeId = model(dbProps.nodeId.name).getInt, modelTypeId = model(dbProps.modelTypeId.name).getInt, 
			removingUpdateId = removingUpdateId))
}

