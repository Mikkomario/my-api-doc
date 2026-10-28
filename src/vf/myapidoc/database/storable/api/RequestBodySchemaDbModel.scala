package vf.myapidoc.database.storable.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.BodySchemaDbProps
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
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with implementation ids
	  */
	lazy val implementationId = property("implementationId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with data types
	  */
	override lazy val dataType = property("dataTypeId")
	/**
	  * Database property used for interacting with model type ids
	  */
	override lazy val modelTypeId = property("modelTypeId")
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
		apply(None, Some(data.originalUpdateId), Some(data.implementationId), data.removingUpdateId, 
			Some(data.dataType), data.modelTypeId, Some(data.isArray), data.customContentType, 
			data.description)
	
	override def withCustomContentType(customContentType: String) = apply(customContentType =customContentType)
	override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withImplementationId(implementationId: Int) = apply(implementationId =Some(implementationId))
	override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: RequestBodySchemaData) = RequestBodySchema(id.getInt, data)
}

/**
  * Used for interacting with RequestBodySchemas in the database
  * @param id request body schema database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class RequestBodySchemaDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                    implementationId: Option[Int] = None, removingUpdateId: Option[Int] = None,
                                    dataType: Option[PropertyType] = None, modelTypeId: Option[Int] = None,
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
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param parentId         parent id to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param dataType         data type to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param modelTypeId      model type id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param isArray          is array to assign to the new model (default = currently assigned 
	  *                         value)
	  */
	override def copyBodySchema(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId, 
		parentId: Option[Int] = parentId, removingUpdateId: Option[Int] = removingUpdateId, 
		dataType: Option[PropertyType] = dataType, modelTypeId: Option[Int] = modelTypeId, 
		isArray: Option[Boolean] = isArray) = 
		copy(id = id, originalUpdateId = originalUpdateId, implementationId = parentId, 
			removingUpdateId = removingUpdateId, dataType = dataType, modelTypeId = modelTypeId, 
			isArray = isArray)
	
	override def withCustomContentType(customContentType: String) = copy(customContentType =customContentType)
	override def withDescription(description: String) = copy(description = description)
	override def withImplementationId(implementationId: Int) = copy(implementationId = Some(implementationId))
	override def withIsArray(isArray: Boolean) = copy(isArray = Some(isArray))
}

