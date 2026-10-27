package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table, TableColumn}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}

object BodySchemaDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                   Table operated using this configuration
	  * @param parentIdPropName        Name of the database property matching parent id (default =  "parentId")
	  * @param dataTypePropName        Name of the database property matching data type (default = "dataTypeId")
	  * @param modelTypeIdPropName     Name of the database property matching model type id (default = "modelTypeId")
	  * @param createdPropName         Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName Name of the database property matching deprecated after
	  *                                (default = "deprecatedAfter")
	  * @param isArrayPropName         Name of the database property matching is array (default = "isArray")
	  * @return A model that defines all body schema database properties
	  */
	def apply(table: Table, parentIdPropName: String = "parentId", dataTypePropName: String = "dataTypeId",
	          modelTypeIdPropName: String = "modelTypeId", createdPropName: String = "created",
	          deprecatedAfterPropName: String = "deprecatedAfter",
	          isArrayPropName: String = "isArray"): BodySchemaDbProps =
		_BodySchemaDbProps(table, parentIdPropName, dataTypePropName, modelTypeIdPropName, createdPropName, 
			deprecatedAfterPropName, isArrayPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                   Table operated using this configuration
	  * @param parentIdPropName        Name of the database property matching parent id (default =  "parentId")
	  * @param dataTypePropName        Name of the database property matching data type (default = "dataTypeId")
	  * @param modelTypeIdPropName     Name of the database property matching model type id (default  = "modelTypeId")
	  * @param createdPropName         Name of the database property matching created (default = "created")
	  * @param deprecatedAfterPropName Name of the database property matching deprecated after 
	  *                                (default = "deprecatedAfter")
	  * @param isArrayPropName         Name of the database property matching is array (default = "isArray")
	  */
	private case class _BodySchemaDbProps(table: Table, parentIdPropName: String = "parentId",
	                                      dataTypePropName: String = "dataTypeId",
	                                      modelTypeIdPropName: String = "modelTypeId",
	                                      createdPropName: String = "created",
	                                      deprecatedAfterPropName: String = "deprecatedAfter",
	                                      isArrayPropName: String = "isArray")
		extends BodySchemaDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override lazy val parentId = DbPropertyDeclaration.from(table, parentIdPropName)
		override lazy val dataType = DbPropertyDeclaration.from(table, dataTypePropName)
		override lazy val modelTypeId = DbPropertyDeclaration.from(table, modelTypeIdPropName)
		override lazy val created = DbPropertyDeclaration.from(table, createdPropName)
		override val deprecatedAfter = DbPropertyDeclaration.from(table, deprecatedAfterPropName)
		override lazy val isArray = DbPropertyDeclaration.from(table, isArrayPropName)
	}
}

/**
  * Common trait for classes which provide access to body schema database properties
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbProps extends HasIdProperty with DeprecatesAfterDefined
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration which defines how parent id shall be interacted with in the database
	  */
	def parentId: DbPropertyDeclaration
	/**
	  * Declaration which defines how data type shall be interacted with in the database
	  */
	def dataType: DbPropertyDeclaration
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
	/**
	  * Declaration which defines how is array shall be interacted with in the database
	  */
	def isArray: DbPropertyDeclaration
	
	
	// IMPLEMENTED	--------------------
	
	override def deprecationColumn: TableColumn = deprecatedAfter.column
}

