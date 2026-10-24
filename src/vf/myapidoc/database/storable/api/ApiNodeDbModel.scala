package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.model.factory.api.ApiNodeFactory
import vf.myapidoc.model.partial.api.ApiNodeData
import vf.myapidoc.model.stored.api.ApiNode

import java.time.Instant

/**
  * Used for constructing ApiNodeDbModel instances and for inserting api nodes to the database
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
object ApiNodeDbModel 
	extends StorableFactory[ApiNodeDbModel, ApiNode, ApiNodeData] with FromIdFactory[Int, ApiNodeDbModel] 
		with HasIdProperty with ApiNodeFactory[ApiNodeDbModel] with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with group ids
	  */
	lazy val groupId = property("groupId")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with names no spaces
	  */
	lazy val nameNoSpaces = property("nameNoSpaces")
	
	/**
	  * Database property used for interacting with relative paths
	  */
	lazy val relativePath = property("relativePath")
	
	/**
	  * Database property used for interacting with parent ids
	  */
	lazy val parentId = property("parentId")
	
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
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNode
	
	override def apply(data: ApiNodeData): ApiNodeDbModel = 
		apply(None, Some(data.groupId), data.name, data.nameNoSpaces, data.relativePath, data.parentId, 
			data.description, Some(data.created), data.deprecatedAfter)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withDescription(description: String) = apply(description = description)
	
	override def withGroupId(groupId: Int) = apply(groupId = Some(groupId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withName(name: String) = apply(name = name)
	
	override def withNameNoSpaces(nameNoSpaces: String) = apply(nameNoSpaces = nameNoSpaces)
	
	override def withParentId(parentId: Int) = apply(parentId = Some(parentId))
	
	override def withRelativePath(relativePath: String) = apply(relativePath = relativePath)
	
	override protected def complete(id: Value, data: ApiNodeData) = ApiNode(id.getInt, data)
}

/**
  * Used for interacting with ApiNodes in the database
  * @param id api node database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeDbModel(id: Option[Int] = None, groupId: Option[Int] = None, name: String = "", 
	nameNoSpaces: String = "", relativePath: String = "", parentId: Option[Int] = None, 
	description: String = "", created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, ApiNodeDbModel] 
		with ApiNodeFactory[ApiNodeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		Vector(ApiNodeDbModel.id.name -> id, ApiNodeDbModel.groupId.name -> groupId, 
			ApiNodeDbModel.name.name -> name, ApiNodeDbModel.nameNoSpaces.name -> nameNoSpaces, 
			ApiNodeDbModel.relativePath.name -> relativePath, ApiNodeDbModel.parentId.name -> parentId, 
			ApiNodeDbModel.description.name -> description, ApiNodeDbModel.created.name -> created, 
			ApiNodeDbModel.deprecatedAfter.name -> deprecatedAfter)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = ApiNodeDbModel.table
	
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	override def withDescription(description: String) = copy(description = description)
	
	override def withGroupId(groupId: Int) = copy(groupId = Some(groupId))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	override def withName(name: String) = copy(name = name)
	
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	
	override def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	
	override def withRelativePath(relativePath: String) = copy(relativePath = relativePath)
}

