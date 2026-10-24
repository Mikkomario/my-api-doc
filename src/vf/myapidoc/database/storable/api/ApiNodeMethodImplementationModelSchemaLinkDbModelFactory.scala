package vf.myapidoc.database.storable.api

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Table}
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.props.api.{ApiNodeMethodImplementationModelSchemaLinkDbProps, ApiNodeMethodImplementationModelSchemaLinkDbPropsWrapper}
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationModelSchemaLinkData
import vf.myapidoc.model.stored.api.ApiNodeMethodImplementationModelSchemaLink

import java.time.Instant

object ApiNodeMethodImplementationModelSchemaLinkDbModelFactory
{
	// OTHER	--------------------
	
	/**
	  * @return A factory for constructing api node method implementation model schema link database models
	  */
	def apply(table: Table, dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps) = 
		ApiNodeMethodImplementationModelSchemaLinkDbModelFactoryImpl(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * Used for constructing ApiNodeMethodImplementationModelSchemaLinkDbModel instances and for 
	  * inserting api node method implementation model schema links to the database
	  * @param table                                             Table targeted by these models
	  * @param apiNodeMethodImplementationModelSchemaLinkDbProps Properties which specify how the 
	  *                                                          database interactions are performed
	  * @author Mikko Hilpinen
	  * @since 22.10.2026, v0.1
	  */
	case class ApiNodeMethodImplementationModelSchemaLinkDbModelFactoryImpl(table: Table, 
		apiNodeMethodImplementationModelSchemaLinkDbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps) 
		extends ApiNodeMethodImplementationModelSchemaLinkDbModelFactory 
			with ApiNodeMethodImplementationModelSchemaLinkDbPropsWrapper with DeprecatesAfterDefined
	{
		// ATTRIBUTES	--------------------
		
		override val id = DbPropertyDeclaration("id", index)
		
		override val deprecationColumn = deprecatedAfter
		
		
		// IMPLEMENTED	--------------------
		
		override def apply(data: ApiNodeMethodImplementationModelSchemaLinkData): ApiNodeMethodImplementationModelSchemaLinkDbModel = 
			
			apply(None, Some(data.implementationId), Some(data.modelTypeId), Some(data.created), 
				data.deprecatedAfter)
		
		override def withCreated(created: Instant) = apply(created = Some(created))
		
		override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
			Some(deprecatedAfter))
		
		override def withId(id: Int) = apply(id = Some(id))
		
		override def withImplementationId(implementationId: Int) = apply(implementationId = 
			Some(implementationId))
		
		override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
		
		override protected def complete(id: Value, data: ApiNodeMethodImplementationModelSchemaLinkData) = 
			ApiNodeMethodImplementationModelSchemaLink(id.getInt, data)
		
		
		// OTHER	--------------------
		
		/**
		  * @param id api node method implementation model schema link database id
		  * @return Constructs a new api node method implementation model schema link database model with the 
		  * specified properties
		  */
		def apply(id: Option[Int] = None, implementationId: Option[Int] = None, 
			modelTypeId: Option[Int] = None, created: Option[Instant] = None, 
			deprecatedAfter: Option[Instant] = None): ApiNodeMethodImplementationModelSchemaLinkDbModel = 
			_ApiNodeMethodImplementationModelSchemaLinkDbModel(table, 
				apiNodeMethodImplementationModelSchemaLinkDbProps, id, implementationId, modelTypeId, 
				created, deprecatedAfter)
	}
	
	/**
	  * Used for interacting with ApiNodeMethodImplementationModelSchemaLinks in the database
	  * @param table   Table interacted with when using this model
	  * @param dbProps Configurations of the interacted database properties
	  * @param id      api node method implementation model schema link database id
	  * @author Mikko Hilpinen
	  * @since 22.10.2026, v0.1
	  */
	private case class _ApiNodeMethodImplementationModelSchemaLinkDbModel(table: Table, 
		dbProps: ApiNodeMethodImplementationModelSchemaLinkDbProps, id: Option[Int] = None, 
		implementationId: Option[Int] = None, modelTypeId: Option[Int] = None, 
		created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
		extends ApiNodeMethodImplementationModelSchemaLinkDbModel
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param id               Id to assign to the new model (default = currently assigned id)
		  * @param implementationId implementation id to assign to the new model (default = currently 
		  *                         assigned value)
		  * @param modelTypeId      model type id to assign to the new model (default = currently 
		  *                         assigned value)
		  * @param created          created to assign to the new model (default = currently assigned 
		  *                         value)
		  * @param deprecatedAfter  deprecated after to assign to the new model (default = currently 
		  *                         assigned value)
		  * @return Copy of this model with the specified api node method implementation model schema link 
		  * properties
		  */
		override protected def copyApiNodeMethodImplementationModelSchemaLink(id: Option[Int] = id, 
			implementationId: Option[Int] = implementationId, modelTypeId: Option[Int] = modelTypeId, 
			created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter) = 
			copy(id = id, implementationId = implementationId, modelTypeId = modelTypeId, created = created, 
				deprecatedAfter = deprecatedAfter)
	}
}

/**
  * Common trait for factories yielding api node method implementation model schema link database 
  * models
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDbModelFactory 
	extends ApiNodeMethodImplementationModelSchemaLinkDbModelFactoryLike[ApiNodeMethodImplementationModelSchemaLinkDbModel, 
		ApiNodeMethodImplementationModelSchemaLink, ApiNodeMethodImplementationModelSchemaLinkData]

