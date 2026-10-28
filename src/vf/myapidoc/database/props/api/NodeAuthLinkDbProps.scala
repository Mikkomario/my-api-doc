package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import utopia.vault.model.template.HasIdProperty

object NodeAuthLinkDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param nodeIdPropName           Name of the database property matching node id (default = 
	  *                                 "nodeId")
	  * @param authIdPropName           Name of the database property matching auth id (default = 
	  *                                 "authId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  * @param methodPropName           Name of the database property matching method (default = 
	  *                                 "method")
	  * @param descriptionPropName      Name of the database property matching description (default = 
	  *                                 "description")
	  * @return A model that defines all node auth link database properties
	  */
	def apply(table: Table, originalUpdateIdPropName: String = "originalUpdateId", 
		nodeIdPropName: String = "nodeId", authIdPropName: String = "authId", 
		removingUpdateIdPropName: String = "removingUpdateId", methodPropName: String = "method", 
		descriptionPropName: String = "description"): NodeAuthLinkDbProps = 
		_NodeAuthLinkDbProps(table, originalUpdateIdPropName, nodeIdPropName, authIdPropName, 
			removingUpdateIdPropName, methodPropName, descriptionPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param nodeIdPropName           Name of the database property matching node id (default = 
	  *                                 "nodeId")
	  * @param authIdPropName           Name of the database property matching auth id (default = 
	  *                                 "authId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  * @param methodPropName           Name of the database property matching method (default = 
	  *                                 "method")
	  * @param descriptionPropName      Name of the database property matching description (default = 
	  *                                 "description")
	  */
	private case class _NodeAuthLinkDbProps(table: Table, 
		originalUpdateIdPropName: String = "originalUpdateId", nodeIdPropName: String = "nodeId", 
		authIdPropName: String = "authId", removingUpdateIdPropName: String = "removingUpdateId", 
		methodPropName: String = "method", descriptionPropName: String = "description") 
		extends NodeAuthLinkDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override lazy val originalUpdateId = DbPropertyDeclaration.from(table, originalUpdateIdPropName)
		
		override lazy val nodeId = DbPropertyDeclaration.from(table, nodeIdPropName)
		
		override lazy val authId = DbPropertyDeclaration.from(table, authIdPropName)
		
		override lazy val removingUpdateId = DbPropertyDeclaration.from(table, removingUpdateIdPropName)
		
		override lazy val method = DbPropertyDeclaration.from(table, methodPropName)
		
		override lazy val description = DbPropertyDeclaration.from(table, descriptionPropName)
	}
}

/**
  * Common trait for classes that provide access to node auth link database properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbProps extends VersionedDocElementDbProps with HasIdProperty
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration that defines how node id shall be interacted with in the database
	  */
	def nodeId: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how auth id shall be interacted with in the database
	  */
	def authId: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how method shall be interacted with in the database
	  */
	def method: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how description shall be interacted with in the database
	  */
	def description: DbPropertyDeclaration
}

