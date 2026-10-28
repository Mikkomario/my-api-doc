package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.VersionedDocElementDbProps
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
	extends VersionedDocElementDbModelFactoryLike[ApiNodeDbModel, ApiNode, ApiNodeData] 
		with ApiNodeFactory[ApiNodeDbModel] with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
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
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	/**
	  * Database property used for interacting with parent ids
	  */
	lazy val parentId = property("parentId")
	/**
	  * Database property used for interacting with descriptions
	  */
	lazy val description = property("description")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.apiNode
	
	override def apply(data: ApiNodeData): ApiNodeDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.groupId), data.name, data.nameNoSpaces, 
			data.relativePath, data.removingUpdateId, data.parentId, data.description)
	
	override def withDescription(description: String) = apply(description = description)
	override def withGroupId(groupId: Int) = apply(groupId = Some(groupId))
	override def withId(id: Int) = apply(id = Some(id))
	override def withName(name: String) = apply(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = apply(nameNoSpaces = nameNoSpaces)
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId =
		Some(originalUpdateId))
	override def withParentId(parentId: Int) = apply(parentId = Some(parentId))
	override def withRelativePath(relativePath: String) = apply(relativePath = relativePath)
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = 
		Some(removingUpdateId))
	
	override protected def complete(id: Value, data: ApiNodeData) = ApiNode(id.getInt, data)
}

/**
  * Used for interacting with ApiNodes in the database
  * @param id api node database id
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None, groupId: Option[Int] = None,
                          name: String = "", nameNoSpaces: String = "", relativePath: String = "",
                          removingUpdateId: Option[Int] = None, parentId: Option[Int] = None, description: String = "")
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[ApiNodeDbModel] 
		with ApiNodeFactory[ApiNodeDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(ApiNodeDbModel.groupId.name -> groupId, 
			ApiNodeDbModel.name.name -> name, ApiNodeDbModel.nameNoSpaces.name -> nameNoSpaces, 
			ApiNodeDbModel.relativePath.name -> relativePath, ApiNodeDbModel.parentId.name -> parentId, 
			ApiNodeDbModel.description.name -> description)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = ApiNodeDbModel
	
	override def table = ApiNodeDbModel.table
	
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
	
	override def withDescription(description: String) = copy(description = description)
	override def withGroupId(groupId: Int) = copy(groupId = Some(groupId))
	override def withId(id: Int) = copy(id = Some(id))
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	override def withRelativePath(relativePath: String) = copy(relativePath = relativePath)
}

