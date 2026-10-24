package vf.myapidoc.database.access.api.node.impl.link.model

import utopia.vault.nosql.view.{FilterableView, FilterableViewWrapper}
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

/**
  * An interface which provides api node method implementation model schema link -based filtering 
  * for other types of access points.
  * @param wrapped Wrapped access point. Expected to include 
  *                api_node_method_implementation_model_schema_link.
  * @param model   A model used for accessing api node method implementation model schema link 
  *                database properties
  * @tparam A Type of the wrapped access class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
case class FilterByApiNodeMethodImplementationModelSchemaLink[+A <: FilterableView[A]](wrapped: A,
                                                                                       model: ApiNodeMethodImplementationModelSchemaLinkDbProps)
	extends FilterApiNodeMethodImplementationModelSchemaLinks[A] with FilterableViewWrapper[A]
{
	// IMPLEMENTED	--------------------
	
	override def table = model.table
}

