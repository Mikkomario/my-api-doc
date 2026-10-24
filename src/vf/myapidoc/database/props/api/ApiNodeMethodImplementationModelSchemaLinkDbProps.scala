package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table, TableColumn}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}

object ApiNodeMethodImplementationModelSchemaLinkDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param implementationIdPropName Name of the database property matching implementation id 
	  *                                 (default = "implementationId")
	  * @param modelTypeIdPropName      Name of the database property matching model type id (default = "modelTypeId")
	  * @param createdPropName          Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName  Name of the database property matching deprecated after 
	  *                                 (default = "deprecatedAfter")
	  * @return A model which defines all api node method implementation model schema link database properties
	  */
	def apply(table: Table, implementationIdPropName: String = "implementationId", 
		modelTypeIdPropName: String = "modelTypeId", createdPropName: String = "created", 
		deprecatedAfterPropName: String = "deprecatedAfter"): ApiNodeMethodImplementationModelSchemaLinkDbProps = 
		_ApiNodeMethodImplementationModelSchemaLinkDbProps(table, implementationIdPropName, 
			modelTypeIdPropName, createdPropName, deprecatedAfterPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param implementationIdPropName Name of the database property matching implementation id 
	  *                                 (default = "implementationId")
	  * @param modelTypeIdPropName      Name of the database property matching model type id (default = "modelTypeId")
	  * @param createdPropName          Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName  Name of the database property matching deprecated after 
	  *                                 (default = "deprecatedAfter")
	  */
	private case class _ApiNodeMethodImplementationModelSchemaLinkDbProps(table: Table, 
		implementationIdPropName: String = "implementationId", modelTypeIdPropName: String = "modelTypeId", 
		createdPropName: String = "created", deprecatedAfterPropName: String = "deprecatedAfter") 
		extends ApiNodeMethodImplementationModelSchemaLinkDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override lazy val implementationId = DbPropertyDeclaration.from(table, implementationIdPropName)
		
		override lazy val modelTypeId = DbPropertyDeclaration.from(table, modelTypeIdPropName)
		
		override lazy val created = DbPropertyDeclaration.from(table, createdPropName)
		
		override lazy val deprecatedAfter = DbPropertyDeclaration.from(table, deprecatedAfterPropName)
	}
}

/**
  * Common trait for classes which provide access to api node method implementation model schema 
  * link database properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbProps extends HasIdProperty with DeprecatesAfterDefined
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration which defines how implementation id shall be interacted with in the database
	  */
	def implementationId: DbPropertyDeclaration
	/**
	  * Declaration which defines how model type id shall be interacted with in the database
	  */
	def modelTypeId: DbPropertyDeclaration
	/**
	  * Declaration which defines how created shall be interacted with in the database
	  */
	def created: DbPropertyDeclaration
	/**
	  * Declaration which defines how deprecated after shall be interacted with in the database
	  */
	def deprecatedAfter: DbPropertyDeclaration
	
	
	// IMPLEMENTED	-----------------
	
	override def deprecationColumn: TableColumn = deprecatedAfter.column
}

