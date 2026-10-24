package vf.myapidoc.database.reader.auth

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.auth.ScopeDbModel
import vf.myapidoc.model.partial.auth.ScopeData
import vf.myapidoc.model.stored.auth.Scope

import scala.util.Success

/**
  * Used for reading scope data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ScopeDbReader extends DbRowReader[Scope] with ParseTableModel[Scope] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = ScopeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(Scope(valid(this.model.id.name).getInt, 
			ScopeData(apiId = valid(this.model.apiId.name).getInt, 
			name = valid(this.model.name.name).getString, 
			created = valid(this.model.created.name).getInstant, 
			deprecatedAfter = valid(this.model.deprecatedAfter.name).instant)))
}

