package vf.myapidoc.database.access.api.params.path

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.PathParametersSchemaLink

object AccessPathParametersSchemaLink 
	extends AccessOneDeprecatingRoot[AccessPathParametersSchemaLink[PathParametersSchemaLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessPathParameterSchemaLinks.all.head
}

/**
  * Used for accessing individual path parameter schema links from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessPathParametersSchemaLink[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessPathParametersSchemaLink[A]] 
		with HasValues[AccessPathParametersSchemaLinkValue] 
		with FilterPathParameterSchemaLinks[AccessPathParametersSchemaLink[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessPathParametersSchemaLinkValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = 
		AccessPathParametersSchemaLink(newTarget)
}

