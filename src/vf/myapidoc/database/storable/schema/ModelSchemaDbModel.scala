package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
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
	extends VersionedDocElementDbModelFactoryLike[ModelSchemaDbModel, ModelSchema, ModelSchemaData]
		with ModelSchemaFactory[ModelSchemaDbModel] with VersionedDocElementDbProps
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
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.modelSchema
	
	override def apply(data: ModelSchemaData): ModelSchemaDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.apiId), data.removingUpdateId, data.name, 
			data.description)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withName(name: String) = apply(name = name)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: ModelSchemaData) = ModelSchema(id.getInt, data)
}

/**
  * Used for interacting with ModelSchemas in the database
  * @param id model schema database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ModelSchemaDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None, 
	apiId: Option[Int] = None, removingUpdateId: Option[Int] = None, name: String = "", 
	description: String = "") 
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[ModelSchemaDbModel]
		with ModelSchemaFactory[ModelSchemaDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(ModelSchemaDbModel.apiId.name -> apiId, 
			ModelSchemaDbModel.name.name -> name, ModelSchemaDbModel.description.name -> description)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ModelSchemaDbModel
	
	override def table = ModelSchemaDbModel.table
	
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
	override def withDescription(description: String) = copy(description = description)
	override def withId(id: Int) = copy(id = Some(id))
	override def withName(name: String) = copy(name = name)
}

