package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.api.AdditionalNodeAuthScopeRequirementFactory

object AdditionalNodeAuthScopeRequirementData 
	extends FromModelFactoryWithSchema[AdditionalNodeAuthScopeRequirementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(PropertyDeclaration("scopeLinkId", IntType, Single("scope_link_id")), 
			PropertyDeclaration("scopeId", IntType, Single("scope_id"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AdditionalNodeAuthScopeRequirementData(valid("scopeLinkId").getInt, valid("scopeId").getInt)
}

/**
  * Links an additional required scope to an auth-scope link
  * @param scopeLinkId ID of the auth-scope link that requires the additional scope
  * @param scopeId     ID of the additional authorization scope required by the node.
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AdditionalNodeAuthScopeRequirementData(scopeLinkId: Int, scopeId: Int) 
	extends AdditionalNodeAuthScopeRequirementFactory[AdditionalNodeAuthScopeRequirementData] 
		with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("scopeLinkId" -> scopeLinkId, "scopeId" -> scopeId))
	
	override def withScopeId(scopeId: Int) = copy(scopeId = scopeId)
	
	override def withScopeLinkId(scopeLinkId: Int) = copy(scopeLinkId = scopeLinkId)
}

