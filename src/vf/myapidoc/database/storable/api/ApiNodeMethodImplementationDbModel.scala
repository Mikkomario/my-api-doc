package vf.myapidoc.database.storable.api

import utopia.access.model.enumeration.Method
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
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
	extends VersionedDocElementDbModelFactoryLike[ApiNodeMethodImplementationDbModel, ApiNodeMethodImplementation, ApiNodeMethodImplementationData] 
		with ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationDbModel] 
		with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with node ids
	  */
	lazy val nodeId = property("nodeId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with methods
	  */
	lazy val method = property("method")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	/**
	  * Database property used for interacting with require request bodies
	  */
	lazy val requiresRequestBody = property("requiresRequestBody")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNodeMethodImplementation
	
	override def apply(data: ApiNodeMethodImplementationData): ApiNodeMethodImplementationDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.nodeId), data.removingUpdateId, Some(data.method), 
			data.description, Some(data.requiresRequestBody))
	
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withMethod(method: Method) = apply(method = Some(method))
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
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
case class ApiNodeMethodImplementationDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                              nodeId: Option[Int] = None, removingUpdateId: Option[Int] = None,
                                              method: Option[Method] = None, description: String = "",
                                              requiresRequestBody: Option[Boolean] = None)
	extends VersionedDocElementDbModel 
		with VersionedDocElementDbModelLike[ApiNodeMethodImplementationDbModel] 
		with ApiNodeMethodImplementationFactory[ApiNodeMethodImplementationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(ApiNodeMethodImplementationDbModel.nodeId.name -> nodeId, 
			ApiNodeMethodImplementationDbModel.method.name -> method.map { _.name }, 
			ApiNodeMethodImplementationDbModel.description.name -> description, 
			ApiNodeMethodImplementationDbModel.requiresRequestBody.name -> requiresRequestBody)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ApiNodeMethodImplementationDbModel
	
	override def table = ApiNodeMethodImplementationDbModel.table
	
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
	
	override def withDescription(description: String) = copy(description = description)
	override def withId(id: Int) = copy(id = Some(id))
	override def withMethod(method: Method) = copy(method = Some(method))
	override def withNodeId(nodeId: Int) = copy(nodeId = Some(nodeId))
	override def withRequiresRequestBody(requiresRequestBody: Boolean) = 
		copy(requiresRequestBody = Some(requiresRequestBody))
}

