package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.ApiNodeGroupFactory
import vf.myapidoc.model.partial.api.ApiNodeGroupData
import vf.myapidoc.model.stored.api.ApiNodeGroup

import java.time.Instant

/**
  * Used for constructing ApiNodeGroupDbModel instances and for inserting api node groups to the 
  * database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ApiNodeGroupDbModel 
	extends StorableFactory[ApiNodeGroupDbModel, ApiNodeGroup, ApiNodeGroupData] 
		with FromIdFactory[Int, ApiNodeGroupDbModel] with HasIdProperty 
		with ApiNodeGroupFactory[ApiNodeGroupDbModel] with DeprecatesAfterDefined
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
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
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
	
	override def table = MyApiDocTables.apiNodeGroup
	
	override def apply(data: ApiNodeGroupData): ApiNodeGroupDbModel = 
		apply(None, Some(data.apiId), data.name, data.description, Some(data.created), data.deprecatedAfter)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: ApiNodeGroupData) = ApiNodeGroup(id.getInt, data)
}

/**
  * Used for interacting with ApiNodeGroups in the database
  * @param id api node group database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeGroupDbModel(id: Option[Int] = None, apiId: Option[Int] = None, name: String = "", 
	description: String = "", created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ApiNodeGroupDbModel] 
		with ApiNodeGroupFactory[ApiNodeGroupDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ApiNodeGroupDbModel.id.name -> id, ApiNodeGroupDbModel.apiId.name -> apiId, 
			ApiNodeGroupDbModel.name.name -> name, ApiNodeGroupDbModel.description.name -> description, 
			ApiNodeGroupDbModel.created.name -> created, 
			ApiNodeGroupDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ApiNodeGroupDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
}

