package vf.myapidoc.database.reader.auth

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.reader.api.VersionedDocElementDbReaderLike
import vf.myapidoc.database.storable.auth.ScopeDbModel
import vf.myapidoc.model.partial.auth.ScopeData
import vf.myapidoc.model.stored.auth.Scope

import scala.util.Success

/**
  * Used for reading scope data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ScopeDbReader extends VersionedDocElementDbReaderLike[Scope]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = ScopeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model            Model from which additional data may be read
	  * @param id               ID to assign to the read/parsed versioned doc element
	  * @param originalUpdateId original update id to assign to the new versioned doc element
	  * @param removingUpdateId removing update id to assign to the new versioned doc element
	  */
	override protected def apply(model: HasProperties, id: Int, originalUpdateId: Int, removingUpdateId: Option[Int]) =
		Scope(id, ScopeData(originalUpdateId = originalUpdateId, apiId = model(dbProps.apiId.name).getInt, 
			name = model(dbProps.name.name).getString, removingUpdateId = removingUpdateId))
}

