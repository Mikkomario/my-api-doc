package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, StandardStoredFactory, StoredModelConvertible}
import vf.myapidoc.database.access.api.node.auth.scope.AccessAdditionalNodeAuthScopeRequirement
import vf.myapidoc.model.factory.api.AdditionalNodeAuthScopeRequirementFactoryWrapper
import vf.myapidoc.model.partial.api.AdditionalNodeAuthScopeRequirementData

object AdditionalNodeAuthScopeRequirement 
	extends StandardStoredFactory[AdditionalNodeAuthScopeRequirementData, AdditionalNodeAuthScopeRequirement]
{
	// ATTRIBUTES	--------------------
	
	override val dataFactory = AdditionalNodeAuthScopeRequirementData
}

/**
  * Represents a additional node auth scope requirement that has already been stored in the 
  * database. 
  * Links an additional required scope to an auth-scope link
  * @param id   ID of this additional node auth scope requirement in the database
  * @param data Wrapped additional node auth scope requirement data
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AdditionalNodeAuthScopeRequirement(id: Int, data: AdditionalNodeAuthScopeRequirementData) 
	extends StoredModelConvertible[AdditionalNodeAuthScopeRequirementData] 
		with FromIdFactory[Int, AdditionalNodeAuthScopeRequirement] 
		with AdditionalNodeAuthScopeRequirementFactoryWrapper[AdditionalNodeAuthScopeRequirementData, AdditionalNodeAuthScopeRequirement]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this additional node auth scope requirement in the database
	  */
	def access = AccessAdditionalNodeAuthScopeRequirement(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AdditionalNodeAuthScopeRequirementData) = copy(data = data)
}

