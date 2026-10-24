package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table, TableColumn}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}

object NodeAuthLinkDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                   Table operated using this configuration
	  * @param nodeIdPropName          Name of the database property matching node id (default = "nodeId")
	  * @param authIdPropName          Name of the database property matching auth id (default = "authId")
	  * @param methodPropName          Name of the database property matching method (default = "method")
	  * @param descriptionPropName     Name of the database property matching description (default = "description")
	  * @param createdPropName         Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName Name of the database property matching deprecated after 
	  *                                (default = "deprecatedAfter")
	  * @return A model which defines all node auth link database properties
	  */
	def apply(table: Table, nodeIdPropName: String = "nodeId", authIdPropName: String = "authId", 
		methodPropName: String = "method", descriptionPropName: String = "description", 
		createdPropName: String = "created", 
		deprecatedAfterPropName: String = "deprecatedAfter"): NodeAuthLinkDbProps = 
		_NodeAuthLinkDbProps(table, nodeIdPropName, authIdPropName, methodPropName, descriptionPropName, 
			createdPropName, deprecatedAfterPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                   Table operated using this configuration
	  * @param nodeIdPropName          Name of the database property matching node id (default = "nodeId")
	  * @param authIdPropName          Name of the database property matching auth id (default = "authId")
	  * @param methodPropName          Name of the database property matching method (default = "method")
	  * @param descriptionPropName     Name of the database property matching description (default = "description")
	  * @param createdPropName         Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName Name of the database property matching deprecated after 
	  *                                (default = "deprecatedAfter")
	  */
	private case class _NodeAuthLinkDbProps(table: Table, nodeIdPropName: String = "nodeId", 
		authIdPropName: String = "authId", methodPropName: String = "method", 
		descriptionPropName: String = "description", createdPropName: String = "created", 
		deprecatedAfterPropName: String = "deprecatedAfter") 
		extends NodeAuthLinkDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override lazy val nodeId = DbPropertyDeclaration.from(table, nodeIdPropName)
		override lazy val authId = DbPropertyDeclaration.from(table, authIdPropName)
		override lazy val method = DbPropertyDeclaration.from(table, methodPropName)
		override lazy val description = DbPropertyDeclaration.from(table, descriptionPropName)
		override lazy val created = DbPropertyDeclaration.from(table, createdPropName)
		override lazy val deprecatedAfter = DbPropertyDeclaration.from(table, deprecatedAfterPropName)
	}
}

/**
  * Common trait for classes which provide access to node auth link database properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait NodeAuthLinkDbProps extends HasIdProperty with DeprecatesAfterDefined
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration which defines how node id shall be interacted with in the database
	  */
	def nodeId: DbPropertyDeclaration
	/**
	  * Declaration which defines how auth id shall be interacted with in the database
	  */
	def authId: DbPropertyDeclaration
	/**
	  * Declaration which defines how method shall be interacted with in the database
	  */
	def method: DbPropertyDeclaration
	/**
	  * Declaration which defines how description shall be interacted with in the database
	  */
	def description: DbPropertyDeclaration
	/**
	  * Declaration which defines how created shall be interacted with in the database
	  */
	def created: DbPropertyDeclaration
	/**
	  * Declaration which defines how deprecated after shall be interacted with in the database
	  */
	def deprecatedAfter: DbPropertyDeclaration
	
	
	// IMPLEMENTED	--------------------
	
	override def deprecationColumn: TableColumn = deprecatedAfter
}

