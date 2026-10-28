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
trait BodySchemaDbModelLike[+Repr] extends VersionedDocElementDbModelLike[Repr] with BodySchemaFactory[Repr]
{
	// ABSTRACT	--------------------
	
	def parentId: Option[Int]
	def dataType: Option[PropertyType]
	def modelTypeId: Option[Int]
	def isArray: Option[Boolean]
	
	/**
	  * Access to the database properties that are utilized in this model
	  */
	override def dbProps: BodySchemaDbProps
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param parentId         parent id to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param dataType         data type to assign to the new model (default = currently assigned 
	  *                         value)
	  * @param modelTypeId      model type id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param isArray          is array to assign to the new model (default = currently assigned 
	  *                         value)
	  * @return Copy of this model with the specified body schema properties
	  */
	protected def copyBodySchema(id: Option[Int] = id, originalUpdateId: Option[Int] = originalUpdateId,
	                             parentId: Option[Int] = parentId, removingUpdateId: Option[Int] = removingUpdateId,
	                             dataType: Option[PropertyType] = dataType, modelTypeId: Option[Int] = modelTypeId,
	                             isArray: Option[Boolean] = isArray): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def valueProperties: Seq[(String, Value)] = 
		super[VersionedDocElementDbModelLike].valueProperties ++ Vector(dbProps.parentId.name -> parentId, 
			dbProps.dataType.name -> dataType.map[Value] { e => e.id }.getOrElse(Value.empty), 
			dbProps.modelTypeId.name -> modelTypeId, dbProps.isArray.name -> isArray)
	
	/**
	  * @param id               ID to assign to the new model (default = currently assigned id)
	  * @param originalUpdateId original update id to assign to the new model (default = currently 
	  *                         assigned value)
	  * @param removingUpdateId removing update id to assign to the new model (default = currently 
	  *                         assigned value)
	  */
	override def copyVersionedDocElement(id: Option[Int] = id, 
		originalUpdateId: Option[Int] = originalUpdateId, removingUpdateId: Option[Int] = removingUpdateId) = 
		copyBodySchema(id = id, originalUpdateId = originalUpdateId, removingUpdateId = removingUpdateId)
	
	override def withDataType(dataType: PropertyType) = copyBodySchema(dataType = Some(dataType))
	override def withId(id: Int) = copyBodySchema(id = Some(id))
	override def withIsArray(isArray: Boolean) = copyBodySchema(isArray = Some(isArray))
	override def withModelTypeId(modelTypeId: Int) = copyBodySchema(modelTypeId = Some(modelTypeId))
	override def withParentId(parentId: Int) = copyBodySchema(parentId = Some(parentId))
}

