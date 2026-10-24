package vf.myapidoc.model.partial.api

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.myapidoc.model.factory.api.ApiNodeFactory

import java.time.Instant

object ApiNodeData extends FromModelFactoryWithSchema[ApiNodeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("groupId", IntType, Single("group_id")), 
			PropertyDeclaration("name", StringType), PropertyDeclaration("nameNoSpaces", StringType, 
			Single("name_no_spaces")), PropertyDeclaration("relativePath", StringType, 
			Single("relative_path")), PropertyDeclaration("parentId", IntType, Single("parent_id"), 
			isOptional = true), PropertyDeclaration("description", StringType, isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Single("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		ApiNodeData(valid("groupId").getInt, valid("name").getString, valid("nameNoSpaces").getString, 
			valid("relativePath").getString, valid("parentId").int, valid("description").getString, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Represents an individual API node implementation
  * @param groupId         ID of the node group this node belongs to
  * @param name            Human-readable name of this node
  * @param nameNoSpaces    A lowercase version of the node name, with no whitespace included. 
  *                        Used for indexing & search.
  * @param relativePath    Path of this node, relative to the parent node (if defined) or the API 
  *                        root (if no parent is defined). 
  *                        May contain path parameters.
  * @param parentId        ID of the node that appears directly above/before this node on the path.
  *                        None if no parent node appears between this node and the API root.
  * @param description     A description of this API node.
  * @param created         Time when this api node was added to the database
  * @param deprecatedAfter Time when this api node became deprecated. None while this api node is still valid.
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class ApiNodeData(groupId: Int, name: String, nameNoSpaces: String, relativePath: String,
                       parentId: Option[Int] = None, description: String = "", created: Instant = Now,
                       deprecatedAfter: Option[Instant] = None)
	extends ApiNodeFactory[ApiNodeData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this api node has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this api node is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	/**
	 * @return Whether this node resides under another documented node
	 */
	def hasParent = parentId.isDefined
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("groupId" -> groupId, "name" -> name, "nameNoSpaces" -> nameNoSpaces,
		"relativePath" -> relativePath, "parentId" -> parentId, "description" -> description,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter))
	
	override def withCreated(created: Instant) = copy(created = created)
	override def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	override def withDescription(description: String) = copy(description = description)
	override def withGroupId(groupId: Int) = copy(groupId = groupId)
	override def withName(name: String) = copy(name = name)
	override def withNameNoSpaces(nameNoSpaces: String) = copy(nameNoSpaces = nameNoSpaces)
	override def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	override def withRelativePath(relativePath: String) = copy(relativePath = relativePath)
}

