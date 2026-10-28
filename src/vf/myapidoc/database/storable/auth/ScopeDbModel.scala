package vf.myapidoc.database.storable.auth

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
import vf.myapidoc.model.factory.auth.ScopeFactory
import vf.myapidoc.model.partial.auth.ScopeData
import vf.myapidoc.model.stored.auth.Scope

import java.time.Instant

/**
  * Used for constructing ScopeDbModel instances and for inserting scopes to the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ScopeDbModel 
	extends VersionedDocElementDbModelFactoryLike[ScopeDbModel, Scope, ScopeData] 
		with ScopeFactory[ScopeDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.scope
	
	override def apply(data: ScopeData): ScopeDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.apiId), data.name, data.removingUpdateId)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	override def withId(id: Int) = apply(id = Some(id))
	override def withName(name: String) = apply(name = name)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
		Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
	
	override protected def complete(id: Value, data: ScopeData) = Scope(id.getInt, data)
}

/**
  * Used for interacting with Scopes in the database
  * @param id scope database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ScopeDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None, apiId: Option[Int] = None,
                        name: String = "", removingUpdateId: Option[Int] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[ScopeDbModel] with ScopeFactory[ScopeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Pair(ScopeDbModel.apiId.name -> apiId, 
			ScopeDbModel.name.name -> name)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ScopeDbModel
	
	override def table = ScopeDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, removingUpdateId: Option[Int] = removingUpdateId) = 
		copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	override def withId(id: Int) = copy(id = Some(id))
	override def withName(name: String) = copy(name = name)
}

