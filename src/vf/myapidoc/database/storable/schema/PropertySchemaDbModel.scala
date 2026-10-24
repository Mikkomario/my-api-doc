package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.schema.PropertySchemaFactory
import vf.myapidoc.model.partial.schema.PropertySchemaData
import vf.myapidoc.model.stored.schema.StoredPropertySchema

import java.time.Instant

/**
  * Used for constructing PropertySchemaDbModel instances and for inserting property schemas to 
  * the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object PropertySchemaDbModel 
	extends StorableFactory[PropertySchemaDbModel, StoredPropertySchema, PropertySchemaData] 
		with FromIdFactory[Int, PropertySchemaDbModel] with HasIdProperty 
		with PropertySchemaFactory[PropertySchemaDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with data types
	  */
	lazy val dataType = property("dataTypeId")
	
	/**
	  * Database property used for interacting with content type ids
	  */
	lazy val contentTypeId = property("contentTypeId")
	
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
	
	/**
	  * Database property used for interacting with are arrays
	  */
	lazy val isArray = property("isArray")
	
	/**
	  * Database property used for interacting with are optional
	  */
	lazy val isOptional = property("isOptional")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.propertySchema
	
	override def apply(data: PropertySchemaData): PropertySchemaDbModel = 
		apply(None, Some(data.modelTypeId), data.name, Some(data.dataType), data.contentTypeId, 
			data.description, Some(data.created), data.deprecatedAfter, Some(data.isArray), 
			Some(data.isOptional))
	
	override def withContentTypeId(contentTypeId: Int) = apply(contentTypeId = Some(contentTypeId))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
	
	override def withIsOptional(isOptional: Boolean) = apply(isOptional = Some(isOptional))
	
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: PropertySchemaData) = StoredPropertySchema(id.getInt, 
		data)
}

/**
  * Used for interacting with PropertySchemas in the database
  * @param id property schema database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PropertySchemaDbModel(id: Option[Int] = None, modelTypeId: Option[Int] = None, name: String = "", 
	dataType: Option[PropertyType] = None, contentTypeId: Option[Int] = None, description: String = "", 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
	isArray: Option[Boolean] = None, isOptional: Option[Boolean] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PropertySchemaDbModel] 
		with PropertySchemaFactory[PropertySchemaDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(PropertySchemaDbModel.id.name -> id, PropertySchemaDbModel.modelTypeId.name -> modelTypeId, 
			PropertySchemaDbModel.name.name -> name, 
			PropertySchemaDbModel.dataType.name -> dataType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			PropertySchemaDbModel.contentTypeId.name -> contentTypeId, 
			PropertySchemaDbModel.description.name -> description, 
			PropertySchemaDbModel.created.name -> created, 
			PropertySchemaDbModel.deprecatedAfter.name -> deprecatedAfter, 
			PropertySchemaDbModel.isArray.name -> isArray, 
			PropertySchemaDbModel.isOptional.name -> isOptional)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = PropertySchemaDbModel.table
	
	override def withContentTypeId(contentTypeId: Int) = copy(contentTypeId = Some(contentTypeId))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDataType(dataType: PropertyType) = copy(dataType = Some(dataType))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withIsArray(isArray: Boolean) = copy(isArray = Some(isArray))
	
	override def withIsOptional(isOptional: Boolean) = copy(isOptional = Some(isOptional))
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	
	override def withName(name: String) = copy(name = name)
}

