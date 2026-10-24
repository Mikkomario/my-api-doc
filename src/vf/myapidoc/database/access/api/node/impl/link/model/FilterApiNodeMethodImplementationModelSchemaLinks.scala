package vf.myapidoc.database.access.api.node.impl.link.model

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.Column
import utopia.vault.nosql.template.IntIndexFilterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.props.api.ApiNodeMethodImplementationModelSchemaLinkDbProps

/**
  * Common trait for access points which may be filtered based on api node method implementation 
  * model schema link properties
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait FilterApiNodeMethodImplementationModelSchemaLinks[+Repr] 
	extends NullDeprecatableView[Repr] with TimelineView[Repr] with IntIndexFilterable[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Model that defines api node method implementation model schema link database properties
	  */
	def model: ApiNodeMethodImplementationModelSchemaLinkDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override protected def idColumn: Column = model.id
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param implementationId implementation id to target
	  * @return Copy of this access point that only includes api node method implementation model schema 
	  * links with the specified implementation id
	  */
	def ofImplementation(implementationId: Int) = filter(model.implementationId.column <=> implementationId)
	
	/**
	  * @param implementationIds Targeted implementation ids
	  * @return Copy of this access point that only includes api node method implementation model schema 
	  * links where implementation id is within the specified value set
	  */
	def ofImplementations(implementationIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.implementationId, implementationIds))
	
	/**
	  * @param modelTypeId model type id to target
	  * @return Copy of this access point that only includes api node method implementation model schema 
	  * links with the specified model type id
	  */
	def ofModelType(modelTypeId: Int) = filter(model.modelTypeId.column <=> modelTypeId)
	
	/**
	  * @param modelTypeIds Targeted model type ids
	  * @return Copy of this access point that only includes api node method implementation model schema 
	  * links where model type id is within the specified value set
	  */
	def ofModelTypes(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId, 
		modelTypeIds))
}

