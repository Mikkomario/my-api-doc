package vf.myapidoc.database.storable.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
import vf.myapidoc.model.factory.api.PathParametersSchemaLinkFactory
import vf.myapidoc.model.partial.api.PathParametersSchemaLinkData
import vf.myapidoc.model.stored.api.PathParametersSchemaLink

import java.time.Instant

/**
  * Used for constructing PathParametersSchemaLinkDbModel instances and for inserting path 
  * parameter schema links to the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object PathParametersSchemaLinkDbModel 
	extends VersionedDocElementDbModelFactoryLike[PathParametersSchemaLinkDbModel, PathParametersSchemaLink, PathParametersSchemaLinkData] 
		with PathParametersSchemaLinkFactory[PathParametersSchemaLinkDbModel] with VersionedDocElementDbProps
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
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.pathParametersSchemaLink
	
	override def apply(data: PathParametersSchemaLinkData): PathParametersSchemaLinkDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.nodeId), Some(data.modelTypeId), 
			data.removingUpdateId)
	
	override def withId(id: Int) = apply(id = Some(id))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: PathParametersSchemaLinkData) = 
		PathParametersSchemaLink(id.getInt, data)
}

/**
  * Used for interacting with PathParameterSchemaLinks in the database
  * @param id path parameters schema link database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PathParametersSchemaLinkDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                           nodeId: Option[Int] = None, modelTypeId: Option[Int] = None,
                                           removingUpdateId: Option[Int] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[PathParametersSchemaLinkDbModel] 
		with PathParametersSchemaLinkFactory[PathParametersSchemaLinkDbModel] with Storable 
		with HasId[Option[Int]] with FromIdFactory[Int, PathParametersSchemaLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Pair(PathParametersSchemaLinkDbModel.nodeId.name -> nodeId, 
			PathParametersSchemaLinkDbModel.modelTypeId.name -> modelTypeId)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = PathParametersSchemaLinkDbModel
	
	override def table = PathParametersSchemaLinkDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId,
	                                     removingUpdateId: Option[Int] = removingUpdateId) =
		copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withId(id: Int) = copy(id = Some(id))
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	override def withNodeId(nodeId: Int) = copy(nodeId = Some(nodeId))
}

