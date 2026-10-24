package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.schema.ModelSchemaFactory
import vf.myapidoc.model.partial.schema.ModelSchemaData
import vf.myapidoc.model.stored.schema.ModelSchema

import java.time.Instant

/**
  * Used for constructing ModelSchemaDbModel instances and for inserting model schemas to the 
  * database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ModelSchemaDbModel 
	extends StorableFactory[ModelSchemaDbModel, ModelSchema, ModelSchemaData] 
		with FromIdFactory[Int, ModelSchemaDbModel] with HasIdProperty 
		with ModelSchemaFactory[ModelSchemaDbModel] with DeprecatesAfterDefined
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
	
	override def table = MyApiDocTables.modelSchema
	
	override def apply(data: ModelSchemaData): ModelSchemaDbModel = 
		apply(None, Some(data.apiId), data.name, data.description, Some(data.created), data.deprecatedAfter)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: ModelSchemaData) = ModelSchema(id.getInt, data)
}

/**
  * Used for interacting with ModelSchemas in the database
  * @param id model schema database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ModelSchemaDbModel(id: Option[Int] = None, apiId: Option[Int] = None, name: String = "", 
	description: String = "", created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ModelSchemaDbModel] 
		with ModelSchemaFactory[ModelSchemaDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ModelSchemaDbModel.id.name -> id, ModelSchemaDbModel.apiId.name -> apiId, 
			ModelSchemaDbModel.name.name -> name, ModelSchemaDbModel.description.name -> description, 
			ModelSchemaDbModel.created.name -> created, 
			ModelSchemaDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ModelSchemaDbModel.table
	
	override def withApiId(apiId: Int) = copy(apiId = Some(apiId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
}

