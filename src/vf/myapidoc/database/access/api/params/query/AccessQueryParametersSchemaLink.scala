package vf.myapidoc.database.access.api.params.query

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneDeprecatingRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.api.QueryParametersSchemaLink

object AccessQueryParametersSchemaLink 
	extends AccessOneDeprecatingRoot[AccessQueryParametersSchemaLink[QueryParametersSchemaLink]]
{
	// ATTRIBUTES	--------------------
	
	override val all = AccessQueryParameterSchemaLinks.all.head
}

/**
  * Used for accessing individual query parameter schema links from the DB at a time
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class AccessQueryParametersSchemaLink[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessQueryParametersSchemaLink[A]] 
		with HasValues[AccessQueryParametersSchemaLinkValue] 
		with FilterQueryParameterSchemaLinks[AccessQueryParametersSchemaLink[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessQueryParametersSchemaLinkValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = 
		AccessQueryParametersSchemaLink(newTarget)
}

