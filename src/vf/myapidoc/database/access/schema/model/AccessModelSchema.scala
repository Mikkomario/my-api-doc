package vf.myapidoc.database.access.schema.model

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.database.MyApiDocTables
import vf.myapidoc.database.access.schema.prop.{AccessPropertySchemaValue, FilterByPropertySchema}
import vf.myapidoc.model.stored.schema.ModelSchema

object AccessModelSchema 
	extends AccessOneDeprecatingRoot[AccessModelSchema[ModelSchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessModelSchemas.all.head
}

/**
  * Used for accessing individual model schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessModelSchema[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessModelSchema[A]] with HasValues[AccessModelSchemaValue] 
		with FilterModelSchemas[AccessModelSchema[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessModelSchemaValue(wrapped)
	
	/**
	  * A copy of this access which also targets property_schema
	  */
	lazy val joinProperty = join(MyApiDocTables.propertySchema)
	
	/**
	  * Access to the values of linked property schemas
	  */
	lazy val property = AccessPropertySchemaValue(joinProperty)
	
	/**
	  * Access to property schema -based filtering functions
	  */
	lazy val whereProperty = FilterByPropertySchema(joinProperty)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessModelSchema(newTarget)
}

