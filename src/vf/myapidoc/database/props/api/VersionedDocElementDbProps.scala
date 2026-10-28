package vf.myapidoc.database.props.api

import utopia.vault.model.immutable.{DbPropertyDeclaration, Table, TableColumn}
import utopia.vault.model.template.{DeprecatesAfterDefined, HasIdProperty}

object VersionedDocElementDbProps
{
	// OTHER	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  * @return A model that defines all versioned doc element database properties
	  */
	def apply(table: Table, originalUpdateIdPropName: String = "originalUpdateId",
	          removingUpdateIdPropName: String = "removingUpdateId"): VersionedDocElementDbProps =
		_VersionedDocElementDbProps(table, originalUpdateIdPropName, removingUpdateIdPropName)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table                    Table operated using this configuration
	  * @param originalUpdateIdPropName Name of the database property matching original update id 
	  *                                 (default = "originalUpdateId")
	  * @param removingUpdateIdPropName Name of the database property matching removing update id 
	  *                                 (default = "removingUpdateId")
	  */
	private case class _VersionedDocElementDbProps(table: Table, originalUpdateIdPropName: String = "originalUpdateId",
	                                               removingUpdateIdPropName: String = "removingUpdateId")
		extends VersionedDocElementDbProps
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		override lazy val originalUpdateId = DbPropertyDeclaration.from(table, originalUpdateIdPropName)
		override lazy val removingUpdateId = DbPropertyDeclaration.from(table, removingUpdateIdPropName)
	}
}

/**
  * Common trait for classes that provide access to versioned doc element database properties
  * @author Mikko Hilpinen
  * @since 28.10.2026, v0.1
  */
trait VersionedDocElementDbProps extends HasIdProperty with DeprecatesAfterDefined
{
	// ABSTRACT	--------------------
	
	/**
	  * Declaration that defines how original update id shall be interacted with in the database
	  */
	def originalUpdateId: DbPropertyDeclaration
	/**
	  * Declaration that defines how removing update id shall be interacted with in the database
	  */
	def removingUpdateId: DbPropertyDeclaration
	
	
	// IMPLEMENTED	--------------------
	
	override def deprecationColumn: TableColumn = removingUpdateId.column
}

