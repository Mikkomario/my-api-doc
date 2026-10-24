package vf.myapidoc.database.access.schema.model

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.api.body.FilterByRequestBodySchema
import vf.myapidoc.database.access.schema.prop.{AccessPropertySchemaValues, FilterByPropertySchema}
import vf.myapidoc.database.reader.schema.{CompleteModelSchemaDbReader, ModelSchemaDbReader}
import vf.myapidoc.database.storable.api.RequestBodySchemaDbModel
import vf.myapidoc.database.storable.schema.{ModelSchemaDbModel, PropertySchemaDbModel}
import vf.myapidoc.model.stored.schema.ModelSchema

object AccessModelSchemas 
	extends DeprecatingWrapRowAccess[AccessModelSchemaRows](ModelSchemaDbModel) 
		with WrapOneToManyAccess[AccessCombinedModelSchemas] 
		with AccessManyDeprecatingRoot[AccessModelSchemaRows[ModelSchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ModelSchemaDbReader).all
	
	/**
	 * Access to model schemas, including linked properties
	 */
	lazy val complete = apply(CompleteModelSchemaDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedModelSchemas(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessModelSchemaRows(access)
}

/**
  * Used for accessing multiple model schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessModelSchemas[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingTimeline[A, Repr, AccessModelSchema[A]] with HasValues[AccessModelSchemaValues] 
		with FilterModelSchemas[Repr] with TargetingManyLike[A, Repr, AccessModelSchema[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessModelSchemaValues(wrapped)
	
	/**
	  * A copy of this access which also targets property_schema
	  */
	lazy val joinProperties = join(MyApiDocTables.propertySchema)
	/**
	  * Access to the values of linked property schemas
	  */
	lazy val properties = AccessPropertySchemaValues(joinProperties)
	/**
	  * Access to property schema -based filtering functions
	  */
	lazy val whereProperties = FilterByPropertySchema(joinProperties)
	
	lazy val joinReferencingProperties = join(PropertySchemaDbModel.contentTypeId.column)
	lazy val whereReferencingProperties = FilterByPropertySchema(joinReferencingProperties)
	
	lazy val joinReferencingRequestBodySchemas = join(RequestBodySchemaDbModel.modelTypeId.column)
	lazy val whereReferencingRequestBodySchemas = FilterByRequestBodySchema(joinReferencingRequestBodySchemas)
}

/**
  * Provides access to row-specific model schema -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessModelSchemaRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessModelSchemas[A, AccessModelSchemaRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessModelSchemaRows[A], AccessModelSchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessModelSchemaRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessModelSchema(target)
}

/**
  * Used for accessing model schema items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedModelSchemas[A](wrapped: TargetingMany[A]) 
	extends AccessModelSchemas[A, AccessCombinedModelSchemas[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedModelSchemas[A], AccessModelSchema[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedModelSchemas(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessModelSchema(target)
}

