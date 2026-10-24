package vf.myapidoc.database.storable.api

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps
import vf.myapidoc.model.factory.api.QueryParametersSchemaLinkFactory
import vf.myapidoc.model.partial.api.QueryParametersSchemaLinkData
import vf.myapidoc.model.stored.api.QueryParametersSchemaLink

import java.time.Instant

/**
  * Used for constructing QueryParametersSchemaLinkDbModel instances and for inserting query 
  * parameter schema links to the database
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
object QueryParametersSchemaLinkDbModel 
	extends ApiNodeMethodImplementationModelSchemaLinkDbModelFactoryLike[QueryParametersSchemaLinkDbModel, QueryParametersSchemaLink, QueryParametersSchemaLinkData] 
		with QueryParametersSchemaLinkFactory[QueryParametersSchemaLinkDbModel] 
		with ApiNodeMethodImplementationModelSchemaLinkDbProps with DeprecatesAfterDefined
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with implementation ids
	  */
	override lazy val implementationId = property("implementationId")
	
	/**
	  * Database property used for interacting with model type ids
	  */
	override lazy val modelTypeId = property("modelTypeId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	override lazy val created = property("created")
	
	/**
	  * Database property used for interacting with deprecation times
	  */
	override lazy val deprecatedAfter = property("deprecatedAfter")
	
	override val deprecationColumn = deprecatedAfter
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.queryParametersSchemaLink
	
	override def apply(data: QueryParametersSchemaLinkData): QueryParametersSchemaLinkDbModel = 
		apply(None, Some(data.implementationId), Some(data.modelTypeId), Some(data.created), 
			data.deprecatedAfter)
	
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = 
		Some(deprecatedAfter))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override def withImplementationId(implementationId: Int) = apply(implementationId = 
		Some(implementationId))
	
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	
	override protected def complete(id: Value, data: QueryParametersSchemaLinkData) = 
		QueryParametersSchemaLink(id.getInt, data)
}

/**
  * Used for interacting with QueryParameterSchemaLinks in the database
  * @param id query parameters schema link database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class QueryParametersSchemaLinkDbModel(id: Option[Int] = None, implementationId: Option[Int] = None, 
	modelTypeId: Option[Int] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends ApiNodeMethodImplementationModelSchemaLinkDbModel 
		with ApiNodeMethodImplementationModelSchemaLinkDbModelLike[QueryParametersSchemaLinkDbModel] 
		with QueryParametersSchemaLinkFactory[QueryParametersSchemaLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = QueryParametersSchemaLinkDbModel
	
	override def table = QueryParametersSchemaLinkDbModel.table
	
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
	  */
	override def copyApiNodeMethodImplementationModelSchemaLink(id: Option[Int] = id, 
		implementationId: Option[Int] = implementationId, modelTypeId: Option[Int] = modelTypeId, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter) = 
		copy(id = id, implementationId = implementationId, modelTypeId = modelTypeId, created = created, 
			deprecatedAfter = deprecatedAfter)
}

