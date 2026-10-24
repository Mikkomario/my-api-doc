package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationFactory
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationData
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementation

import java.time.Instant

/**
  * Used for constructing ApiNodeMethodImplementationDbModel instances and for inserting api node 
  * method implementations to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object ApiNodeMethodImplementationDbModel 
	extends StorableFactory[ApiNodeMethodImplementationDbModel, ApiNodeMethodImplementation, ApiNodeMethodImplementationData] 
		with FromIdFactory[Int, ApiNodeMethodImplementationDbModel] with HasIdProperty 
		with ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationDbModel] 
		with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with node ids
	  */
	lazy val nodeId = property("nodeId")
	
	/**
	  * Database property used for interacting with methods
	  */
	lazy val method = property("method")
	
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
	  * Database property used for interacting with require request bodies
	  */
	lazy val requiresRequestBody = property("requiresRequestBody")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNodeMethodImplementation
	
	override def apply(data: ApiNodeMethodImplementationData): ApiNodeMethodImplementationDbModel = 
		apply(None, Some(data.nodeId), Some(data.method), data.description, Some(data.created), 
			data.deprecatedAfter, Some(data.requiresRequestBody))
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withMethod(method: Method) = apply(method = Some(method))
	
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		apply(requiresRequestBody = Some(requiresRequestBody))
	
	override protected def complete(id: Value, data: ApiNodeMethodImplementationData) = 
		ApiNodeMethodImplementation(id.getInt, data)
}

/**
  * Used for interacting with ApiNodeMethodImplementations in the database
  * @param id api node method implementation database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class ApiNodeMethodImplementationDbModel(id: Option[Int] = None, nodeId: Option[Int] = None, 
	method: Option[Method] = None, description: String = "", created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None, requiresRequestBody: Option[Boolean] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ApiNodeMethodImplementationDbModel] 
		with ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ApiNodeMethodImplementationDbModel.id.name -> id, 
			ApiNodeMethodImplementationDbModel.nodeId.name -> nodeId, 
			ApiNodeMethodImplementationDbModel.method.name -> method.map { _.name }, 
			ApiNodeMethodImplementationDbModel.description.name -> description, 
			ApiNodeMethodImplementationDbModel.created.name -> created, 
			ApiNodeMethodImplementationDbModel.deprecatedAfter.name -> deprecatedAfter, 
			ApiNodeMethodImplementationDbModel.requiresRequestBody.name -> requiresRequestBody)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ApiNodeMethodImplementationDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withMethod(method: Method) = copy(method = Some(method))
	
	override def withNodeId(nodeId: Int) = copy(nodeId = Some(nodeId))
	
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		copy(requiresRequestBody = Some(requiresRequestBody))
}

