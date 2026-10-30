package vf.myapidoc.database.access.example.prop

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.example.ExamplePropertyValueDbModel

/**
  * Common trait for access points that may be filtered based on example property value properties
  * @author Mikko Hilpinen
  * @since 30.10.2026, v0.1
  */
trait FilterExamplePropertyValues[+Repr] extends Filterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines example property value database properties
	  */
	def model = ExamplePropertyValueDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param responseId response id to target
	  * @return Copy of this access point that only includes example property values with the specified 
	  * response id
	  */
	def inResponse(responseId: Int) = filter(model.responseId.column <=> responseId)
	
	/**
	  * @param responseIds Targeted response ids
	  * @return Copy of this access point that only includes example property values where response id is 
	  * within the specified value set
	  */
	def inResponses(responseIds: IterableOnce[Int]) = filter(Condition.indexIn(model.responseId, responseIds))
	
	/**
	  * @param propIds Targeted prop ids
	  * @return Copy of this access point that only includes example property values where prop id is within 
	  * the specified value set
	  */
	def ofProperties(propIds: IterableOnce[Int]) = filter(Condition.indexIn(model.propId, propIds))
	
	/**
	  * @param propId prop id to target
	  * @return Copy of this access point that only includes example property values with the specified prop 
	  * id
	  */
	def ofProperty(propId: Int) = filter(model.propId.column <=> propId)
}

