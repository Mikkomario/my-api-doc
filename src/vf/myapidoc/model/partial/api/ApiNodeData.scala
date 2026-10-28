package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.myapidoc.model.factory.api.ApiNodeFactory

import java.time.Instant

object ApiNodeData extends FromModelFactoryWithSchema[ApiNodeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("originalUpdateId", IntType, Single("original_update_id")),
		PropertyDeclaration("groupId", IntType, Single("group_id")),
		PropertyDeclaration("name", StringType),
		PropertyDeclaration("nameNoSpaces", StringType, Single("name_no_spaces")),
		PropertyDeclaration("relativePath", StringType, Single("relative_path")),
		PropertyDeclaration("removingUpdateId", IntType, Single("removing_update_id"), isOptional = true),
		PropertyDeclaration("parentId", IntType, Single("parent_id"), isOptional = true),
		PropertyDeclaration("description", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeData(valid("originalUpdateId").getInt, valid("groupId").getInt, valid("name").getString, 
			valid("nameNoSpaces").getString, valid("relativePath").getString, valid("removingUpdateId").int, 
			valid("parentId").int, valid("description").getString)
}

/**
  * Represents an individual API node implementation
  * @param originalUpdateId ID of the update that introduced this element (version)
  * @param groupId          ID of the node group this node belongs to
  * @param name             Human-readable name of this node
  * @param nameNoSpaces     A lowercase version of the node name, with no whitespace included. 
  *                         Used for indexing & search.
  * @param relativePath     Path of this node, relative to the parent node (if defined) or the 
  *                         API root (if no parent is defined). 
  *                         May contain path parameters.
  * @param removingUpdateId ID of the update that removed or replaced this element (version)
  * @param parentId         ID of the node that appears directly above/before this node on the 
  *                         path. 
  *                         None if no parent node appears between this node and the API root.
  * @param description      A description of this API node.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeData(originalUpdateId: Int, groupId: Int, name: String, nameNoSpaces: String,
                       relativePath: String, removingUpdateId: Option[Int] = None, parentId: Option[Int] = None,
                       description: String = "")
	extends ApiNodeFactory[ApiNodeData] with VersionedDocElementData with VersionedDocElementDataLike[ApiNodeData]
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this node resides under another documented node
	  */
	def hasParent = parentId.isDefined
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = super[VersionedDocElementData].toModel ++ Model(Vector("groupId" -> groupId, "name" -> name,
		"nameNoSpaces" -> nameNoSpaces, "relativePath" -> relativePath, "parentId" -> parentId,
		"description" -> description))
	
	override def copyVersionedDocElement(originalUpdateId: Int, removingUpdateId: Option[Int]) = 
		copy(originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDescription(description: String) = copy(description = description)
	override def withGroupId(groupId: Int) = copy(groupId = groupId)
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	override def withRelativePath(relativePath: String) = copy(relativePath = relativePath)
}

