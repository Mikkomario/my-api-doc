package vf.myapidoc.database.storable.api

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.ResponseBodySchemaFactory
import vf.myapidoc.model.partial.api.ResponseBodySchemaData
import vf.myapidoc.model.stored.api.ResponseBodySchema

import java.time.Instant

/**
  * Used for constructing ResponseBodySchemaDbModel instances and for inserting response body 
  * schemas to the database
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
object ResponseBodySchemaDbModel 
	extends BodySchemaDbModelFactoryLike[ResponseBodySchemaDbModel, ResponseBodySchema, ResponseBodySchemaData] 
		with ResponseBodySchemaFactory[ResponseBodySchemaDbModel] with BodySchemaDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with status ids
	  */
	lazy val statusId = property("statusId")
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
	
	
	// IMPLEMENTED	--------------------
	
	override def parentId = statusId
	
	override def table = MyApiDocTables.responseBodySchema
	
	override def apply(data: ResponseBodySchemaData): ResponseBodySchemaDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.statusId), data.removingUpdateId, 
			Some(data.dataType), data.modelTypeId, Some(data.isArray))
	
	override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
	override def withId(id: Int) = apply(id = Some(id))
	override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	override def withStatusId(statusId: Int) = apply(statusId = Some(statusId))
	
	override protected def complete(id: Value, data: ResponseBodySchemaData) = ResponseBodySchema(id.getInt, data)
}

/**
  * Used for interacting with ResponseBodySchemas in the database
  * @param id response body schema database id
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class ResponseBodySchemaDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                     statusId: Option[Int] = None, removingUpdateId: Option[Int] = None,
                                     dataType: Option[PropertyType] = None, modelTypeId: Option[Int] = None,
                                     isArray: Option[Boolean] = None)
	extends BodySchemaDbModel with BodySchemaDbModelLike[ResponseBodySchemaDbModel] 
		with ResponseBodySchemaFactory[ResponseBodySchemaDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = ResponseBodySchemaDbModel
	
	override def parentId = statusId
	
	override def table = ResponseBodySchemaDbModel.table
	
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
		copy(id = id, originalUpdateId = originalUpdateId, statusId = parentId, 
			removingUpdateId = removingUpdateId, dataType = dataType, modelTypeId = modelTypeId, 
			isArray = isArray)
	
	override def withStatusId(statusId: Int) = copy(statusId = Some(statusId))
}

