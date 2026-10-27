package vf.myapidoc.database.storable.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.{ApiNodeMethodImplementationModelSchemaLinkDbProps, BodySchemaDbProps}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.RequestBodySchemaFactory
import vf.myapidoc.model.partial.api.RequestBodySchemaData
import vf.myapidoc.model.stored.api.RequestBodySchema

import java.time.Instant

/**
  * Used for constructing RequestBodySchemaDbModel instances and for inserting request body 
  * schemas to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object RequestBodySchemaDbModel 
	extends BodySchemaDbModelFactoryLike[RequestBodySchemaDbModel, RequestBodySchema, RequestBodySchemaData] 
		with RequestBodySchemaFactory[RequestBodySchemaDbModel] with BodySchemaDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with implementation ids
	  */
	override lazy val implementationId = property("implementationId")
	/**
	  * Database property used for interacting with data types
	  */
	override lazy val dataType = property("dataTypeId")
	/**
	  * Database property used for interacting with model type ids
	  */
	override lazy val modelTypeId = property("modelTypeId")
	/**
	  * Database property used for interacting with creation times
	  */
	override lazy val created = property("created")
	/**
	  * Database property used for interacting with deprecation times
	  */
	override val deprecatedAfter = property("deprecatedAfter")
	/**
	  * Database property used for interacting with are arrays
	  */
	override lazy val isArray = property("isArray")
	/**
	  * Database property used for interacting with custom content types
	  */
	lazy val customContentType = property("customContentType")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def parentId = implementationId
	
	override def table = MyApiDocTables.requestBodySchema
	
	override def apply(data: RequestBodySchemaData): RequestBodySchemaDbModel = 
		apply(None, Some(data.implementationId), Some(data.dataType), data.modelTypeId, Some(data.created), 
			data.deprecatedAfter, Some(data.isArray), data.customContentType, data.description)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	override def withCustomContentType(customContentType: String) = 
		apply(customContentType =customContentType)
	override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter =Some(deprecatedAfter))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withImplementationId(implementationId: Int) = apply(implementationId =Some(implementationId))
	override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override protected def complete(id: Value, data: RequestBodySchemaData) = RequestBodySchema(id.getInt, data)
}

/**
  * Used for interacting with RequestBodySchemas in the database
  * @param id request body schema database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class RequestBodySchemaDbModel(id: Option[Int] = None, implementationId: Option[Int] = None,
                                    dataType: Option[PropertyType] = None, modelTypeId: Option[Int] = None,
                                    created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None,
                                    isArray: Option[Boolean] = None, customContentType: String = "",
                                    description: String = "")
	extends BodySchemaDbModel with BodySchemaDbModelLike[RequestBodySchemaDbModel] 
		with RequestBodySchemaFactory[RequestBodySchemaDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[BodySchemaDbModelLike].valueProperties ++ Pair(RequestBodySchemaDbModel.customContentType.name -> customContentType, 
			RequestBodySchemaDbModel.description.name -> description)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = RequestBodySchemaDbModel
	
	override def parentId = implementationId
	
	override def table = RequestBodySchemaDbModel.table
	
	/**
	  * @param id              Id to assign to the new model (default = currently assigned id)
	  * @param parentId        parent id to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param dataType        data type to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param modelTypeId     model type id to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param created         created to assign to the new model (default = currently assigned value)
	  * @param deprecatedAfter deprecated after to assign to the new model (default = currently 
	  *                        assigned value)
	  * @param isArray         is array to assign to the new model (default = currently assigned 
	  *                        value)
	  */
	override def copyBodySchema(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		dataType: Option[PropertyType] = dataType, modelTypeId: Option[Int] = modelTypeId, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter, 
		isArray: Option[Boolean] = isArray) = 
		copy(id = id, implementationId = parentId, dataType = dataType, modelTypeId = modelTypeId, 
			created = created, deprecatedAfter = deprecatedAfter, isArray = isArray)
	
	override def withCustomContentType(customContentType: String) = copy(customContentType =customContentType)
	override def withDescription(description: String) = copy(description = description)
	override def withImplementationId(implementationId: Int) = copy(implementationId = Some(implementationId))
	override def withIsArray(isArray: Boolean) = copy(isArray = Some(isArray))
}

