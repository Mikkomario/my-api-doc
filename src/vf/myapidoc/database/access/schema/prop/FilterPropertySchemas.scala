package vf.myapidoc.database.access.schema.prop

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.schema.PropertySchemaDbModel

/**
  * Common trait for access points which may be filtered based on property schema properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterPropertySchemas[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines property schema database properties
	  */
	def model = PropertySchemaDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param contentTypeId content type id to target
	  * @return Copy of this access point that only includes property schemas with the specified content type 
	  * id
	  */
	def containingModelOfType(contentTypeId: Int) = filter(model.contentTypeId.column <=> contentTypeId)
	
	/**
	  * @param contentTypeIds Targeted content type ids
	  * @return Copy of this access point that only includes property schemas where content type id is within 
	  * the specified value set
	  */
	def containingModelsOfType(contentTypeIds: IterableOnce[Int]) = 
		filter(Condition.indexIn(model.contentTypeId, contentTypeIds))
	
	/**
	  * @param modelTypeId model type id to target
	  * @return Copy of this access point that only includes property schemas with the specified model type id
	  */
	def ofModelType(modelTypeId: Int) = filter(model.modelTypeId.column <=> modelTypeId)
	
	/**
	  * @param modelTypeIds Targeted model type ids
	  * @return Copy of this access point that only includes property schemas where model type id is within 
	  * the specified value set
	  */
	def ofModelTypes(modelTypeIds: IterableOnce[Int]) = filter(Condition.indexIn(model.modelTypeId,
		modelTypeIds))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes property schemas with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes property schemas where name is within the 
	  * specified value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

