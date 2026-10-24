package vf.myapidoc.database.reader.api

import utopia.flow.generic.model.immutable.Model
import utopia.vault.model.template.HasTableAsTarget
import utopia.vault.nosql.read.DbRowReader
import utopia.vault.nosql.read.parse.ParseTableModel
import vf.myapidoc.database.storable.api.AdditionalNodeAuthScopeRequirementDbModel
import vf.myapidoc.model.partial.api.AdditionalNodeAuthScopeRequirementData
import vf.myapidoc.model.stored.api.AdditionalNodeAuthScopeRequirement

import scala.util.Success

/**
  * Used for reading additional node auth scope requirement data from the DB
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object AdditionalNodeAuthScopeRequirementDbReader 
	extends DbRowReader[AdditionalNodeAuthScopeRequirement] 
		with ParseTableModel[AdditionalNodeAuthScopeRequirement] with HasTableAsTarget
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = AdditionalNodeAuthScopeRequirementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def fromValid(valid: Model) = 
		Success(AdditionalNodeAuthScopeRequirement(valid(this.model.id.name).getInt, 
			AdditionalNodeAuthScopeRequirementData(scopeLinkId = valid(this.model.scopeLinkId.name).getInt, 
			scopeId = valid(this.model.scopeId.name).getInt)))
}

