package vf.myapidoc.database.storable.auth

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
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
	extends StorableFactory[ScopeDbModel, Scope, ScopeData] with FromIdFactory[Int, ScopeDbModel] 
		with HasIdProperty with ScopeFactory[ScopeDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	lazy val deprecatedAfter = property("deprecatedAfter")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.scope
	
	override def apply(data: ScopeData): ScopeDbModel = 
		apply(None, Some(data.apiId), data.name, Some(data.created), data.deprecatedAfter)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter =Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: ScopeData) = Scope(id.getInt, data)
}

/**
  * Used for interacting with Scopes in the database
  * @param id scope database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ScopeDbModel(id: Option[Int] = None, apiId: Option[Int] = None, name: String = "", 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ScopeDbModel] 
		with ScopeFactory[ScopeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ScopeDbModel.id.name -> id, ScopeDbModel.apiId.name -> apiId, ScopeDbModel.name.name -> name, 
			ScopeDbModel.created.name -> created, ScopeDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ScopeDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
}

