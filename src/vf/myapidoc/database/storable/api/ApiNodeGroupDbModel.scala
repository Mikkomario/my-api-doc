package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.factory.api.ApiNodeGroupFactory
import vf.myapidoc.model.partial.api.ApiNodeGroupData
import vf.myapidoc.model.stored.api.ApiNodeGroup

import java.time.Instant

/**
  * Used for constructing ApiNodeGroupDbModel instances and for inserting api node groups to the 
  * database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ApiNodeGroupDbModel 
	extends VersionedDocElementDbModelFactoryLike[ApiNodeGroupDbModel, ApiNodeGroup, ApiNodeGroupData]
		with ApiNodeGroupFactory[ApiNodeGroupDbModel] with VersionedDocElementDbProps
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
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNodeGroup
	
	override def apply(data: ApiNodeGroupData): ApiNodeGroupDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.apiId), data.name, data.removingUpdateId, 
			data.description)
	
	override def withApiId(apiId: Int) = apply(apiId = Some(apiId))
	override def withDescription(description: String) = apply(description = description)
	override def withId(id: Int) = apply(id = Some(id))
	override def withName(name: String) = apply(name = name)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
		Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
	
	override protected def complete(id: Value, data: ApiNodeGroupData) = ApiNodeGroup(id.getInt, data)
}

/**
  * Used for interacting with ApiNodeGroups in the database
  * @param id api node group database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeGroupDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None, apiId: Option[Int] = None,
                               name: String = "", removingUpdateId: Option[Int] = None, description: String = "")
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[ApiNodeGroupDbModel] 
		with ApiNodeGroupFactory[ApiNodeGroupDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(ApiNodeGroupDbModel.apiId.name -> apiId, 
			ApiNodeGroupDbModel.name.name -> name, ApiNodeGroupDbModel.description.name -> description)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ApiNodeGroupDbModel
	override def table = ApiNodeGroupDbModel.table
	
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

