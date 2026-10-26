package vf.myapidoc.database.access.schema.prop.value

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.schema.PossiblePropertyValueDbModel

/**
  * Common trait for access points which may be filtered based on possible property value 
  * properties
  * @author Mikko Hilpinen
  * @since 26.10.2026, v0.1
  */
trait FilterPossiblePropertyValues[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines possible property value database properties
	  */
	def model = PossiblePropertyValueDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param propIds Targeted prop ids
	  * @return Copy of this access point that only includes possible property values where prop id is within 
	  * the specified value set
	  */
	def ofProperties(propIds: IterableOnce[Int]) = filter(Condition.indexIn(model.propId, propIds))
	
	/**
	  * @param propId prop id to target
	  * @return Copy of this access point that only includes possible property values with the specified prop 
	  * id
	  */
	def ofProperty(propId: Int) = filter(model.propId.column <=> propId)
}

