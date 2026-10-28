package vf.myapidoc.database.access.api.body

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.Column
import utopia.vault.nosql.template.IntIndexFilterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.access.api.update.FilterVersionedDocElements
import vf.myapidoc.database.props.api.BodySchemaDbProps

/**
  * Common trait for access points that may be filtered based on body schema properties
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait FilterBodySchemas[+Repr] extends FilterVersionedDocElements[Repr] with IntIndexFilterable[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Model that defines body schema database properties
	  */
	def model: BodySchemaDbProps
	
	
	// IMPLEMENTED	--------------------
	
	override protected def idColumn: Column = model.id
	
	
	// OTHER	--------------------
	
	/**
	  * @param parentId parent id to target
	  * @return Copy of this access point that only includes body schemas with the specified parent id
	  */
	def ofParent(parentId: Int) = filter(model.parentId.column <=> parentId)
	/**
	  * @param parentIds Targeted parent ids
	  * @return Copy of this access point that only includes body schemas where parent id is within the 
	  * specified value set
	  */
	def ofParents(parentIds: IterableOnce[Int]) = filter(Condition.indexIn(model.parentId, parentIds))
}

