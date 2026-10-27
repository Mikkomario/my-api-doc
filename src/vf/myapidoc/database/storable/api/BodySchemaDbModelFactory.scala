package vf.myapidoc.database.storable.api

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.props.api.{BodySchemaDbProps, BodySchemaDbPropsWrapper}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.partial.api.BodySchemaData
import vf.myapidoc.model.stored.api.BodySchema

import java.time.Instant

object BodySchemaDbModelFactory
{
	// OTHER	--------------------
	
	/**
	  * @return A factory for constructing body schema database models
	  */
	def apply(table: Table, dbProps: BodySchemaDbProps) = BodySchemaDbModelFactoryImpl(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * Used for constructing BodySchemaDbModel instances and for inserting body schemas to the 
	  * database
	  * @param table             Table targeted by these models
	  * @param bodySchemaDbProps Properties which specify how the database interactions are performed
	  * @author Mikko Hilpinen
	  * @since 27.10.2026, v0.1
	  */
	case class BodySchemaDbModelFactoryImpl(table: Table, bodySchemaDbProps: BodySchemaDbProps) 
		extends BodySchemaDbModelFactory with BodySchemaDbPropsWrapper with DeprecatesAfterDefined
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		
		// IMPLEMENTED	--------------------
		
		override def apply(data: BodySchemaData): BodySchemaDbModel = 
			apply(None, Some(data.parentId), Some(data.dataType), data.modelTypeId, Some(data.created), 
				data.deprecatedAfter, Some(data.isArray))
		
		override def withCreated(created: Instant) = apply(created = Some(created))
		
		override def withDataType(dataType: PropertyType) = apply(dataType = Some(dataType))
		
		override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
			Some(deprecatedAfter))
		
		override def withId(id: Int) = apply(id = Some(id))
		
		override def withIsArray(isArray: Boolean) = apply(isArray = Some(isArray))
		
		override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
		
		override def withParentId(parentId: Int) = apply(parentId = Some(parentId))
		
		override protected def complete(id: Value, data: BodySchemaData) = BodySchema(id.getInt, data)
		
		
		// OTHER	--------------------
		
		/**
		  * @param id body schema database id
		  * @return Constructs a new body schema database model with the specified properties
		  */
		def apply(id: Option[Int] = None, parentId: Option[Int] = None, 
			dataType: Option[PropertyType] = None, modelTypeId: Option[Int] = None, 
			created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
			isArray: Option[Boolean] = None): BodySchemaDbModel = 
			_BodySchemaDbModel(table, bodySchemaDbProps, id, parentId, dataType, modelTypeId, created, 
				deprecatedAfter, isArray)
	}
	
	/**
	  * Used for interacting with BodySchemas in the database
	  * @param table   Table interacted with when using this model
	  * @param dbProps Configurations of the interacted database properties
	  * @param id      body schema database id
	  * @author Mikko Hilpinen
	  * @since 27.10.2026, v0.1
	  */
	private case class _BodySchemaDbModel(table: Table, dbProps: BodySchemaDbProps, id: Option[Int] = None, 
		parentId: Option[Int] = None, dataType: Option[PropertyType] = None, modelTypeId: Option[Int] = None, 
		created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
		isArray: Option[Boolean] = None) 
		extends BodySchemaDbModel
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param id              Id to assign to the new model (default = currently assigned id)
		  * @param parentId        parent id to assign to the new model (default = currently assigned 
		  *                        value)
		  * @param dataType        data type to assign to the new model (default = currently assigned 
		  *                        value)
		  * @param modelTypeId     model type id to assign to the new model (default = currently assigned 
		  *                        value)
		  * @param created         created to assign to the new model (default = currently assigned value)
		  * @param deprecatedAfter deprecated after to assign to the new model (default = currently 
		  *                        assigned value)
		  * @param isArray         is array to assign to the new model (default = currently assigned 
		  *                        value)
		  * @return Copy of this model with the specified body schema properties
		  */
		override protected def copyBodySchema(id: Option[Int] = id, parentId: Option[Int] = parentId, 
			dataType: Option[PropertyType] = dataType, modelTypeId: Option[Int] = modelTypeId, 
			created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter, 
			isArray: Option[Boolean] = isArray) = 
			copy(id = id, parentId = parentId, dataType = dataType, modelTypeId = modelTypeId, 
				created = created, deprecatedAfter = deprecatedAfter, isArray = isArray)
	}
}

/**
  * Common trait for factories yielding body schema database models
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbModelFactory 
	extends BodySchemaDbModelFactoryLike[BodySchemaDbModel, BodySchema, BodySchemaData]

