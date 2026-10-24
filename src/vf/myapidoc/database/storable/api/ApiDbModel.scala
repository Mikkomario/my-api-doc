package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.ApiFactory
import vf.myapidoc.model.partial.api.ApiData
import vf.myapidoc.model.stored.api.Api

import java.time.Instant

/**
  * Used for constructing ApiDbModel instances and for inserting apis to the database
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
object ApiDbModel 
	extends StorableFactory[ApiDbModel, Api, ApiData] with FromIdFactory[Int, ApiDbModel] with HasIdProperty 
		with ApiFactory[ApiDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.api
	
	override def apply(data: ApiData): ApiDbModel = apply(None, data.name, Some(data.created))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: ApiData) = Api(id.getInt, data)
}

/**
  * Used for interacting with Apis in the database
  * @param id api database id
  * @author Mikko Hilpinen
  * @since 16.10.2026, v0.1
  */
case class ApiDbModel(id: Option[Int] = None, name: String = "", created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ApiDbModel] with ApiFactory[ApiDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ApiDbModel.id.name -> id, ApiDbModel.name.name -> name, ApiDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ApiDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
}

