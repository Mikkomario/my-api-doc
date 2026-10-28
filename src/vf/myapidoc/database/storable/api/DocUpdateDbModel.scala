package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.DocUpdateFactory
import vf.myapidoc.model.partial.api.DocUpdateData
import vf.myapidoc.model.stored.api.DocUpdate

import java.time.Instant

/**
  * Used for constructing DocUpdateDbModel instances and for inserting doc updates to the database
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
object DocUpdateDbModel 
	extends StorableFactory[DocUpdateDbModel, DocUpdate, DocUpdateData] 
		with FromIdFactory[Int, DocUpdateDbModel] with HasIdProperty with DocUpdateFactory[DocUpdateDbModel]
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with api ids
	  */
	lazy val apiId = property("apiId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.docUpdate
	
	override def apply(data: DocUpdateData): DocUpdateDbModel = apply(None, Some(data.apiId), Some(data.created))
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	override def withCreated(created: Instant) = apply(created = Some(created))
	override def withId(id: Int) = apply(id = Some(id))
	
	override protected def complete(id: Value, data: DocUpdateData) = DocUpdate(id.getInt, data)
}

/**
  * Used for interacting with DocUpdates in the database
  * @param id doc update database id
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
case class DocUpdateDbModel(id: Option[Int] = None, apiId: Option[Int] = None, 
	created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, DocUpdateDbModel] 
		with DocUpdateFactory[DocUpdateDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(DocUpdateDbModel.id.name -> id, DocUpdateDbModel.apiId.name -> apiId, 
			DocUpdateDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = DocUpdateDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
}

