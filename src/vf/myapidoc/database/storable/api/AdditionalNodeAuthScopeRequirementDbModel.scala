package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.AdditionalNodeAuthScopeRequirementFactory
import vf.myapidoc.model.partial.api.AdditionalNodeAuthScopeRequirementData
import vf.myapidoc.model.stored.api.AdditionalNodeAuthScopeRequirement

/**
  * Used for constructing AdditionalNodeAuthScopeRequirementDbModel instances and for inserting 
  * additional node auth scope requirements to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object AdditionalNodeAuthScopeRequirementDbModel 
	extends StorableFactory[AdditionalNodeAuthScopeRequirementDbModel, AdditionalNodeAuthScopeRequirement, AdditionalNodeAuthScopeRequirementData] 
		with FromIdFactory[Int, AdditionalNodeAuthScopeRequirementDbModel] with HasIdProperty 
		with AdditionalNodeAuthScopeRequirementFactory[AdditionalNodeAuthScopeRequirementDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with scope link ids
	  */
	lazy val scopeLinkId = property("scopeLinkId")
	
	/**
	  * Database property used for interacting with scope ids
	  */
	lazy val scopeId = property("scopeId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.additionalNodeAuthScopeRequirement
	
	override def apply(data: AdditionalNodeAuthScopeRequirementData): AdditionalNodeAuthScopeRequirementDbModel = 
		
		apply(None, Some(data.scopeLinkId), Some(data.scopeId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withScopeId(scopeId: Int) = apply(scopeId = Some(scopeId))
	
	override def withScopeLinkId(scopeLinkId: Int) = apply(scopeLinkId = Some(scopeLinkId))
	
	override protected def complete(id: Value, data: AdditionalNodeAuthScopeRequirementData) = 
		AdditionalNodeAuthScopeRequirement(id.getInt, data)
}

/**
  * Used for interacting with AdditionalNodeAuthScopeRequirements in the database
  * @param id additional node auth scope requirement database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AdditionalNodeAuthScopeRequirementDbModel(id: Option[Int] = None, scopeLinkId: Option[Int] = None, 
	scopeId: Option[Int] = None) 
	extends Storable with HasId[Option[Int]] 
		with FromIdFactory[Int, AdditionalNodeAuthScopeRequirementDbModel] 
		with AdditionalNodeAuthScopeRequirementFactory[AdditionalNodeAuthScopeRequirementDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(AdditionalNodeAuthScopeRequirementDbModel.id.name -> id, 
			AdditionalNodeAuthScopeRequirementDbModel.scopeLinkId.name -> scopeLinkId, 
			AdditionalNodeAuthScopeRequirementDbModel.scopeId.name -> scopeId)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = AdditionalNodeAuthScopeRequirementDbModel.table
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withScopeId(scopeId: Int) = copy(scopeId = Some(scopeId))
	
	override def withScopeLinkId(scopeLinkId: Int) = copy(scopeLinkId = Some(scopeLinkId))
}

