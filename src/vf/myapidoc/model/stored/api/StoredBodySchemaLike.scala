package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.enumeration.PropertyType
import vf.myapidoc.model.factory.api.BodySchemaFactoryWrapper
import vf.myapidoc.model.partial.api.BodySchemaDataLike

import java.time.Instant

/**
  * Common trait for body schemas which have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait StoredBodySchemaLike[Data <: BodySchemaDataLike[Data], +Repr] 
	extends Stored[Data, Int] with StoredModelConvertible[Data] with FromIdFactory[Int, Repr] 
		with BodySchemaFactoryWrapper[Data, Repr] with BodySchemaDataLike[Repr]
{
	// IMPLEMENTED	--------------------
	
	override def created = data.created
	
	override def dataType = data.dataType
	
	override def deprecatedAfter = data.deprecatedAfter
	
	override def isArray = data.isArray
	
	override def modelTypeId = data.modelTypeId
	
	override def parentId = data.parentId
	
	override protected def wrappedFactory = data
	
	override def copyBodySchema(parentId: Int, dataType: PropertyType, modelTypeId: Option[Int], 
		created: Instant, deprecatedAfter: Option[Instant], isArray: Boolean) = 
		wrap(data.copyBodySchema(parentId, dataType, modelTypeId, created, deprecatedAfter, isArray))
}

