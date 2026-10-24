package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.ResponseContentTypeFactory
import vf.myapidoc.model.partial.api.ResponseContentTypeData
import vf.myapidoc.model.stored.api.ResponseContentType

import java.time.Instant

/**
  * Used for constructing ResponseContentTypeDbModel instances and for inserting response content 
  * types to the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ResponseContentTypeDbModel 
	extends StorableFactory[ResponseContentTypeDbModel, ResponseContentType, ResponseContentTypeData] 
		with FromIdFactory[Int, ResponseContentTypeDbModel] with HasIdProperty 
		with ResponseContentTypeFactory[ResponseContentTypeDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with status ids
	  */
	lazy val statusId = property("statusId")
	
	/**
	  * Database property used for interacting with content types
	  */
	lazy val contentType = property("contentTypeId")
	
	/**
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	
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
	  * Database property used for interacting with are empty
	  */
	lazy val isEmpty = property("isEmpty")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.responseContentType
	
	override def apply(data: ResponseContentTypeData): ResponseContentTypeDbModel = 
		apply(None, Some(data.statusId), data.contentType, data.modelTypeId, Some(data.created), 
			data.deprecatedAfter, Some(data.isArray), Some(data.isEmpty))
	
	override def withContentType(contentType: PropertyType) = apply(contentType = Some(contentType))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
	
	override def withIsEmpty(isEmpty: Boolean) = apply(isEmpty = Some(isEmpty))
	
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	
	override def withStatusId(statusId: Int) = apply(statusId = Some(statusId))
	
	override protected def complete(id: Value, data: ResponseContentTypeData) = 
		ResponseContentType(id.getInt, data)
}

/**
  * Used for interacting with ResponseContentTypes in the database
  * @param id response content type database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ResponseContentTypeDbModel(id: Option[Int] = None, statusId: Option[Int] = None, 
	contentType: Option[PropertyType] = None, modelTypeId: Option[Int] = None, 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
	isArray: Option[Boolean] = None, isEmpty: Option[Boolean] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ResponseContentTypeDbModel] 
		with ResponseContentTypeFactory[ResponseContentTypeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ResponseContentTypeDbModel.id.name -> id, 
			ResponseContentTypeDbModel.statusId.name -> statusId, 
			ResponseContentTypeDbModel.contentType.name -> contentType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			ResponseContentTypeDbModel.modelTypeId.name -> modelTypeId, 
			ResponseContentTypeDbModel.created.name -> created, 
			ResponseContentTypeDbModel.deprecatedAfter.name -> deprecatedAfter, 
			ResponseContentTypeDbModel.isArray.name -> isArray, 
			ResponseContentTypeDbModel.isEmpty.name -> isEmpty)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ResponseContentTypeDbModel.table
	
	override def withContentType(contentType: PropertyType) = copy(contentType = Some(contentType))
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withIsArray(isArray: Boolean) = copy(isArray = Some(isArray))
	
	override def withIsEmpty(isEmpty: Boolean) = copy(isEmpty = Some(isEmpty))
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	
	override def withStatusId(statusId: Int) = copy(statusId = Some(statusId))
}

