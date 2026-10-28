package vf.myapidoc.database.storable.schema

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.UncertainBoolean
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.database.storable.api.{VersionedDocElementDbModel, VersionedDocElementDbModelFactoryLike, VersionedDocElementDbModelLike}
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
	extends VersionedDocElementDbModelFactoryLike[PropertySchemaDbModel, StoredPropertySchema, PropertySchemaData] 
		with PropertySchemaFactory[PropertySchemaDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
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
	  * Database property used for interacting with are arrays
	  */
	lazy val isArray = property("isArray")
	/**
	  * Database property used for interacting with are optional
	  */
	lazy val isOptional = property("isOptional")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.propertySchema
	
	override def apply(data: PropertySchemaData): PropertySchemaDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.modelTypeId), data.removingUpdateId, data.name, 
			Some(data.dataType), data.contentTypeId, data.description, data.isArray.exact, 
			Some(data.isOptional))
	
	override def withContentTypeId(contentTypeId: Int) = apply(contentTypeId = Some(contentTypeId))
	override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withIsArray(isArray: UncertainBoolean) = apply(isArray = isArray.exact)
	override def withIsOptional(isOptional: Boolean) = apply(isOptional = Some(isOptional))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override def withName(name: String) = apply(name = name)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: PropertySchemaData) = StoredPropertySchema(id.getInt, data)
}

/**
  * Used for interacting with PropertySchemas in the database
  * @param id property schema database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PropertySchemaDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                 modelTypeId: Option[Int] = None, removingUpdateId: Option[Int] = None,
                                 name: String = "", dataType: Option[PropertyType] = None,
                                 contentTypeId: Option[Int] = None, description: String = "",
                                 isArray: Option[Boolean] = None, isOptional: Option[Boolean] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[PropertySchemaDbModel] 
		with PropertySchemaFactory[PropertySchemaDbModel] with Storable with HasId[Option[Int]] 
		with FromIdFactory[Int, PropertySchemaDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(PropertySchemaDbModel.modelTypeId.name -> modelTypeId, 
			PropertySchemaDbModel.name.name -> name, 
			PropertySchemaDbModel.dataType.name -> dataType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			PropertySchemaDbModel.contentTypeId.name -> contentTypeId, 
			PropertySchemaDbModel.description.name -> description, 
			PropertySchemaDbModel.isArray.name -> isArray, 
			PropertySchemaDbModel.isOptional.name -> isOptional)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = PropertySchemaDbModel
	
	override def table = PropertySchemaDbModel.table
	
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
	
	override def withContentTypeId(contentTypeId: Int) = copy(contentTypeId = Some(contentTypeId))
	override def withDataType(dataType: PropertyType) = copy(dataType = Some(dataType))
	override def withDescription(description: String) = copy(description = description)
	override def withId(id: Int) = copy(id = Some(id))
	override def withIsArray(isArray: UncertainBoolean) = copy(isArray = isArray.exact)
	override def withIsOptional(isOptional: Boolean) = copy(isOptional = Some(isOptional))
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	override def withName(name: String) = copy(name = name)
}

