package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
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
	extends StorableFactory[PathParametersSchemaLinkDbModel, PathParametersSchemaLink, PathParametersSchemaLinkData] 
		with FromIdFactory[Int, PathParametersSchemaLinkDbModel] with HasIdProperty 
		with PathParametersSchemaLinkFactory[PathParametersSchemaLinkDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with node ids
	  */
	lazy val nodeId = property("nodeId")
	
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
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.pathParametersSchemaLink
	
	override def apply(data: PathParametersSchemaLinkData): PathParametersSchemaLinkDbModel = 
		apply(None, Some(data.nodeId), Some(data.modelTypeId), Some(data.created), data.deprecatedAfter)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	
	override def withNodeId(nodeId: Int) = apply(nodeId = Some(nodeId))
	
	override protected def complete(id: Value, data: PathParametersSchemaLinkData) = 
		PathParametersSchemaLink(id.getInt, data)
}

/**
  * Used for interacting with PathParameterSchemaLinks in the database
  * @param id path parameters schema link database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class PathParametersSchemaLinkDbModel(id: Option[Int] = None, nodeId: Option[Int] = None, 
	modelTypeId: Option[Int] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PathParametersSchemaLinkDbModel] 
		with PathParametersSchemaLinkFactory[PathParametersSchemaLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(PathParametersSchemaLinkDbModel.id.name -> id, 
			PathParametersSchemaLinkDbModel.nodeId.name -> nodeId, 
			PathParametersSchemaLinkDbModel.modelTypeId.name -> modelTypeId, 
			PathParametersSchemaLinkDbModel.created.name -> created, 
			PathParametersSchemaLinkDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = PathParametersSchemaLinkDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
	
	override def withNodeId(nodeId: Int) = copy(nodeId = Some(nodeId))
}

