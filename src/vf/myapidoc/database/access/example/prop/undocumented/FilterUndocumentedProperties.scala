package vf.myapidoc.database.access.example.prop.undocumented

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.template.Filterable
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.example.UndocumentedPropertyDbModel

/**
  * Common trait for access points that may be filtered based on undocumented property properties
  * @author Mikko Hilpinen
  * @since 12.11.2026, v0.1
  */
trait FilterUndocumentedProperties[+Repr] extends Filterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines undocumented property database properties
	  */
	def model = UndocumentedPropertyDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param modelTypeId model type id to target
	  * @return Copy of this access point that only includes undocumented properties with the specified model 
	  * type id
	  */
	def inModel(modelTypeId: Int) = filter(model.modelTypeId.column <=> modelTypeId)
	
	/**
	  * @param modelTypeIds Targeted model type ids
	  * @return Copy of this access point that only includes undocumented properties where model type id is 
	  * within the specified value set
	  */
	def inModels(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId, modelTypeIds))
	
	/**
	  * @param responseId response id to target
	  * @return Copy of this access point that only includes undocumented properties with the specified 
	  * response id
	  */
	def inResponse(responseId: Int) = filter(model.responseId.column <=> responseId)
	
	/**
	  * @param responseIds Targeted response ids
	  * @return Copy of this access point that only includes undocumented properties where response id is 
	  * within the specified value set
	  */
	def inResponses(responseIds: IterableOnce[Int]) = filter(Condition.indexIn(model.responseId, responseIds))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes undocumented properties with the specified name
	  */
	def named(name: String) = filter(model.name.column <=> name)
	
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes undocumented properties where name is within the 
	  * specified value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

