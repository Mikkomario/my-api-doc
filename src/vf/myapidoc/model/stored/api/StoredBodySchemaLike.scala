package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.{BodySchemaFactory, BodySchemaFactoryWrapper}
import vf.myapidoc.model.partial.api.{BodySchemaDataLike, VersionedDocElementData}

/**
  * Common trait for body schemas that have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait StoredBodySchemaLike[Data <: BodySchemaDataLike[Data], +Repr <: VersionedDocElementData]
	extends BodySchemaFactory[Repr] with BodySchemaDataLike[Repr] 
		with StoredVersionedDocElementLike[Data, Repr] with BodySchemaFactoryWrapper[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def dataType = data.dataType
	override def isArray = data.isArray
	override def modelTypeId = data.modelTypeId
	override def parentId = data.parentId
	
	override def copyBodySchema(originalUpdateId: Int, parentId: Int, removingUpdateId: Option[Int],
	                            dataType: PropertyType, modelTypeId: Option[Int], isArray: Boolean) =
		wrap(data.copyBodySchema(originalUpdateId, parentId, removingUpdateId, dataType, modelTypeId, 
			isArray))
}

