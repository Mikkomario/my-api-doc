package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.factory.api.ApiNodePathFactory
import vf.myapidoc.model.partial.api.ApiNodePathData
import vf.myapidoc.model.stored.api.ApiNodePath

/**
  * Used for constructing ApiNodePathDbModel instances and for inserting api node paths to the 
  * database
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
object ApiNodePathDbModel 
	extends VersionedDocElementDbModelFactoryLike[ApiNodePathDbModel, ApiNodePath, ApiNodePathData] 
		with ApiNodePathFactory[ApiNodePathDbModel] with VersionedDocElementDbProps
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
	  * Database property used for interacting with relatives
	  */
	lazy val relative = property("relative")
	
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	
	/**
	  * Database property used for interacting with params model schema ids
	  */
	lazy val paramsModelSchemaId = property("paramsModelSchemaId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNodePath
	
	override def apply(data: ApiNodePathData): ApiNodePathDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.nodeId), data.relative, data.removingUpdateId, 
			data.paramsModelSchemaId)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = 
		Some(originalUpdateId))
	
	override def withParamsModelSchemaId(paramsModelSchemaId: Int) = 
		apply(paramsModelSchemaId = Some(paramsModelSchemaId))
	
	override def withRelative(relative: String) = apply(relative = relative)
	
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
	
	override protected def complete(id: Value, data: ApiNodePathData) = ApiNodePath(id.getInt, data)
}

/**
  * Used for interacting with ApiNodePaths in the database
  * @param id api node path database id
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
case class ApiNodePathDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None, 
	nodeId: Option[Int] = None, relative: String = "", removingUpdateId: Option[Int] = None, 
	paramsModelSchemaId: Option[Int] = None) 
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[ApiNodePathDbModel] 
		with ApiNodePathFactory[ApiNodePathDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(ApiNodePathDbModel.nodeId.name -> nodeId, 
			ApiNodePathDbModel.relative.name -> relative, 
			ApiNodePathDbModel.paramsModelSchemaId.name -> paramsModelSchemaId)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ApiNodePathDbModel
	
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
	
	override def withNodeId(nodeId: Int) = copy(nodeId = Some(nodeId))
	
	override def withParamsModelSchemaId(paramsModelSchemaId: Int) = 
		copy(paramsModelSchemaId = Some(paramsModelSchemaId))
	
	override def withRelative(relative: String) = copy(relative = relative)
}

