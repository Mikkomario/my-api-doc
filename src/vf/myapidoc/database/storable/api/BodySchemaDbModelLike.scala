package vf.myapidoc.database.storable.api

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.Storable
import utopia.vault.store.{FromIdFactory, HasId}
import vf.myapidoc.database.props.api.BodySchemaDbProps
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.BodySchemaFactory

import java.time.Instant

/**
  * Common trait for database models used for interacting with body schema data in the database
  * @tparam Repr Type of this DB model
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait BodySchemaDbModelLike[+Repr] 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, Repr] with BodySchemaFactory[Repr]
{
	// ABSTRACT	--------------------
	
	def parentId: Option[Int]
	
	def dataType: Option[PropertyType]
	
	def modelTypeId: Option[Int]
	
	def created: Option[Instant]
	
	def deprecatedAfter: Option[Instant]
	
	def isArray: Option[Boolean]
	
	/**
	  * Access to the database properties which are utilized in this model
	  */
	def dbProps: BodySchemaDbProps
	
	/**
	  * @param id              Id to assign to the new model (default = currently assigned id)
	  * @param parentId        parent id to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param dataType        data type to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param modelTypeId     model type id to assign to the new model (default = currently assigned 
	  *                        value)
	  * @param created         created to assign to the new model (default = currently assigned value)
	  * @param deprecatedAfter deprecated after to assign to the new model (default = currently 
	  *                        assigned value)
	  * @param isArray         is array to assign to the new model (default = currently assigned 
	  *                        value)
	  * @return Copy of this model with the specified body schema properties
	  */
	protected def copyBodySchema(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		dataType: Option[PropertyType] = dataType, modelTypeId: Option[Int] = modelTypeId, 
		created: Option[Instant] = created, deprecatedAfter: Option[Instant] = deprecatedAfter, 
		isArray: Option[Boolean] = isArray): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def valueProperties: Seq[(String, Value)] = 
		Vector(dbProps.id.name -> id, dbProps.parentId.name -> parentId, 
			dbProps.dataType.name -> dataType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			dbProps.modelTypeId.name -> modelTypeId, dbProps.created.name -> created, 
			dbProps.deprecatedAfter.name -> deprecatedAfter, dbProps.isArray.name -> isArray)
	
	override def withCreated(created: Instant) = copyBodySchema(created = Some(created))
	
	override def withDataType(dataType: PropertyType) = copyBodySchema(dataType = Some(dataType))
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyBodySchema(deprecatedAfter = Some(deprecatedAfter))
	
	override def withId(id: Int) = copyBodySchema(id = Some(id))
	
	override def withIsArray(isArray: Boolean) = copyBodySchema(isArray = Some(isArray))
	
	override def withModelTypeId(modelTypeId: Int) = copyBodySchema(modelTypeId = Some(modelTypeId))
	
	override def withParentId(parentId: Int) = copyBodySchema(parentId = Some(parentId))
}

