package vf.myapidoc.database.access.api.body

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.RequestBodySchema

object AccessRequestBodySchema extends AccessOneDeprecatingRoot[AccessRequestBodySchema[RequestBodySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessRequestBodySchemas.all.head
}

/**
  * Used for accessing individual request body schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessRequestBodySchema[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessRequestBodySchema[A]] 
		with HasValues[AccessRequestBodySchemaValue] with FilterRequestBodySchemas[AccessRequestBodySchema[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessRequestBodySchemaValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessRequestBodySchema(newTarget)
}

