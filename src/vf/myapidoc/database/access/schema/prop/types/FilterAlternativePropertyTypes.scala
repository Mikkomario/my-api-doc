package vf.myapidoc.database.access.schema.prop.types

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.schema.AlternativePropertyTypeDbModel

/**
  * Common trait for access points which may be filtered based on alternative property type 
  * properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterAlternativePropertyTypes[+Repr] 
	extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines alternative property type database properties
	  */
	def model = AlternativePropertyTypeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param propIds Targeted prop ids
	  * @return Copy of this access point that only includes alternative property types where prop id is 
	  * within the specified value set
	  */
	def ofProperties(propIds: IterableOnce[Int]) = filter(Condition.indexIn(model.propId, propIds))
	
	/**
	  * @param propId prop id to target
	  * @return Copy of this access point that only includes alternative property types with the specified 
	  * prop id
	  */
	def ofProperty(propId: Int) = filter(model.propId.column <=> propId)
}

