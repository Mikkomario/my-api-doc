package vf.myapidoc.database.reader.auth

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.auth.AuthTokenTypeDbModel
import vf.myapidoc.model.enumeration.TokenUseType
import vf.myapidoc.model.partial.auth.AuthTokenTypeData
import vf.myapidoc.model.stored.auth.AuthTokenType

/**
  * Used for reading auth token type data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object AuthTokenTypeDbReader 
	extends DbRowReader[AuthTokenType] with ParseTableModel[AuthTokenType] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = AuthTokenTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = {
		TokenUseType.fromValue(valid(this.model.useType.name)).map { useType => 
			AuthTokenType(valid(this.model.id.name).getInt, 
				AuthTokenTypeData(apiId = valid(this.model.apiId.name).getInt, 
				name = valid(this.model.name.name).getString, 
				nameNoSpaces = valid(this.model.nameNoSpaces.name).getString, useType = useType, 
				created = valid(this.model.created.name).getInstant, 
				deprecatedAfter = valid(this.model.deprecatedAfter.name).instant))
		}
	}
}

