package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import utopia.vault.model.template.HasIdProperty

object BodySchemaDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param parentIdPropName         Name of the database property matching parent id (default = 
	  *                                 "parentId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  * @param dataTypePropName         Name of the database property matching data type (default = 
	  *                                 "dataTypeId")
	  * @param modelTypeIdPropName      Name of the database property matching model type id (default 
	  *                                 = "modelTypeId")
	  * @param isArrayPropName          Name of the database property matching is array (default = 
	  *                                 "isArray")
	  * @return A model that defines all body schema database properties
	  */
	def apply(table: Table, originalUpdateIdPropName: String = "originalUpdateId", 
		parentIdPropName: String = "parentId", removingUpdateIdPropName: String = "removingUpdateId", 
		dataTypePropName: String = "dataTypeId", modelTypeIdPropName: String = "modelTypeId", 
		isArrayPropName: String = "isArray"): BodySchemaDbProps = 
		_BodySchemaDbProps(table, originalUpdateIdPropName, parentIdPropName, removingUpdateIdPropName, 
			dataTypePropName, modelTypeIdPropName, isArrayPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param parentIdPropName         Name of the database property matching parent id (default = 
	  *                                 "parentId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  * @param dataTypePropName         Name of the database property matching data type (default = 
	  *                                 "dataTypeId")
	  * @param modelTypeIdPropName      Name of the database property matching model type id (default 
	  *                                 = "modelTypeId")
	  * @param isArrayPropName          Name of the database property matching is array (default = 
	  *                                 "isArray")
	  */
	private case class _BodySchemaDbProps(table: Table, 
		originalUpdateIdPropName: String = "originalUpdateId", parentIdPropName: String = "parentId", 
		removingUpdateIdPropName: String = "removingUpdateId", dataTypePropName: String = "dataTypeId", 
		modelTypeIdPropName: String = "modelTypeId", isArrayPropName: String = "isArray") 
		extends BodySchemaDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override lazy val originalUpdateId = DbPropertyDeclaration.from(table, originalUpdateIdPropName)
		
		override lazy val parentId = DbPropertyDeclaration.from(table, parentIdPropName)
		
		override lazy val removingUpdateId = DbPropertyDeclaration.from(table, removingUpdateIdPropName)
		
		override lazy val dataType = DbPropertyDeclaration.from(table, dataTypePropName)
		
		override lazy val modelTypeId = DbPropertyDeclaration.from(table, modelTypeIdPropName)
		
		override lazy val isArray = DbPropertyDeclaration.from(table, isArrayPropName)
	}
}

/**
  * Common trait for classes that provide access to body schema database properties
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbProps extends VersionedDocElementDbProps with HasIdProperty
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration that defines how parent id shall be interacted with in the database
	  */
	def parentId: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how data type shall be interacted with in the database
	  */
	def dataType: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how model type id shall be interacted with in the database
	  */
	def modelTypeId: DbPropertyDeclaration
	
	/**
	  * Declaration that defines how is array shall be interacted with in the database
	  */
	def isArray: DbPropertyDeclaration
}

