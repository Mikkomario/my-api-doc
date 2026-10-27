package vf.myapidoc.database.access.schema.prop

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.schema.StoredPropertySchema

object AccessPropertySchema extends AccessOneDeprecatingRoot[AccessPropertySchema[StoredPropertySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessPropertySchemas.all.head
}

/**
  * Used for accessing individual property schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPropertySchema[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessPropertySchema[A]] with HasValues[AccessPropertySchemaValue] 
		with FilterPropertySchemas[AccessPropertySchema[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPropertySchemaValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessPropertySchema(newTarget)
}

