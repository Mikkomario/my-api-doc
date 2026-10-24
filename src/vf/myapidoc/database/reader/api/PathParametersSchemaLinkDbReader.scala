package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
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
object PathParametersSchemaLinkDbReader 
	extends DbRowReader[PathParametersSchemaLink] with ParseTableModel[PathParametersSchemaLink] 
		with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = PathParametersSchemaLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(PathParametersSchemaLink(valid(this.model.id.name).getInt, 
			PathParametersSchemaLinkData(nodeId = valid(this.model.nodeId.name).getInt, 
			modelTypeId = valid(this.model.modelTypeId.name).getInt, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

