package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.ResponseBodySchema

object AccessResponseBodySchema extends AccessOneDeprecatingRoot[AccessResponseBodySchema[ResponseBodySchema]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessResponseBodySchemas.all.head
}

/**
  * Used for accessing individual response body schemas from the DB at a time
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
case class AccessResponseBodySchema[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessResponseBodySchema[A]] 
		with HasValues[AccessResponseBodySchemaValue] 
		with FilterResponseBodySchemas[AccessResponseBodySchema[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessResponseBodySchemaValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessResponseBodySchema(newTarget)
}

