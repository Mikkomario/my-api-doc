package vf.myapidoc.database.storable.api

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.model.template.DeprecatesAfterDefined
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.props.api.{ApiNodeMethodImplementationModelSchemaLinkDbProps, VersionedDocElementDbProps}
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
	extends VersionedDocElementDbModelFactoryLike[QueryParametersSchemaLinkDbModel, QueryParametersSchemaLink, QueryParametersSchemaLinkData] 
		with QueryParametersSchemaLinkFactory[QueryParametersSchemaLinkDbModel] 
		with VersionedDocElementDbProps
{
	// ATTRIBUTES	--------------------
	
	override val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with original update ids
	  */
	override lazy val originalUpdateId = property("originalUpdateId")
	/**
	  * Database property used for interacting with implementation ids
	  */
	lazy val implementationId = property("implementationId")
	/**
	  * Database property used for interacting with model type ids
	  */
	lazy val modelTypeId = property("modelTypeId")
	/**
	  * Database property used for interacting with removing update ids
	  */
	override lazy val removingUpdateId = property("removingUpdateId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = MyApiDocTables.queryParametersSchemaLink
	
	override def apply(data: QueryParametersSchemaLinkData): QueryParametersSchemaLinkDbModel = 
		apply(None, Some(data.originalUpdateId), Some(data.implementationId), Some(data.modelTypeId), 
			data.removingUpdateId)
	
	override def withId(id: Int) = apply(id = Some(id))
	override def withImplementationId(implementationId: Int) = apply(implementationId =Some(implementationId))
	override def withModelTypeId(modelTypeId: Int) = apply(modelTypeId = Some(modelTypeId))
	override def withOriginalUpdateId(originalUpdateId: Int) = apply(originalUpdateId = Some(originalUpdateId))
	override def withRemovingUpdateId(removingUpdateId: Int) = apply(removingUpdateId = Some(removingUpdateId))
	
	override protected def complete(id: Value, data: QueryParametersSchemaLinkData) = 
		QueryParametersSchemaLink(id.getInt, data)
}

/**
  * Used for interacting with QueryParameterSchemaLinks in the database
  * @param id query parameters schema link database id
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class QueryParametersSchemaLinkDbModel(id: Option[Int] = None, originalUpdateId: Option[Int] = None,
                                            implementationId: Option[Int] = None, modelTypeId: Option[Int] = None,
                                            removingUpdateId: Option[Int] = None)
	extends VersionedDocElementDbModel with VersionedDocElementDbModelLike[QueryParametersSchemaLinkDbModel] 
		with QueryParametersSchemaLinkFactory[QueryParametersSchemaLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Pair(QueryParametersSchemaLinkDbModel.implementationId.name -> implementationId, 
			QueryParametersSchemaLinkDbModel.modelTypeId.name -> modelTypeId)
	
	
	// IMPLEMENTED	--------------------
	
	override def dbProps = QueryParametersSchemaLinkDbModel
	
	override def table = QueryParametersSchemaLinkDbModel.table
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, removingUpdateId: Option[Int] = removingUpdateId) = 
		copy(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withImplementationId(implementationId: Int) = copy(implementationId = Some(implementationId))
	override def withModelTypeId(modelTypeId: Int) = copy(modelTypeId = Some(modelTypeId))
}

