package vf.myapidoc.model.stored.api

import utopia.vault.store.{FromIdFactory, Stored, StoredModelConvertible}
import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationModelSchemaLinkFactoryWrapper
import vf.myapidoc.model.partial.api.ApiNodeMethodImplementationModelSchemaLinkDataLike

import java.time.Instant

/**
  * Common trait for api node method implementation model schema links which have been stored in 
  * the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait StoredApiNodeMethodImplementationModelSchemaLinkLike[Data <: ApiNodeMethodImplementationModelSchemaLinkDataLike[Data], 
	+Repr] 
	extends Stored[Data, Int] with StoredModelConvertible[Data] with FromIdFactory[Int, Repr] 
		with ApiNodeMethodImplementationModelSchemaLinkFactoryWrapper[Data, Repr] 
		with ApiNodeMethodImplementationModelSchemaLinkDataLike[Repr]
{
	// IMPLEMENTED	--------------------
	
	override def created = data.created
	
	override def deprecatedAfter = data.deprecatedAfter
	
	override def implementationId = data.implementationId
	
	override def modelTypeId = data.modelTypeId
	
	override protected def wrappedFactory = data
	
	override def copyApiNodeMethodImplementationModelSchemaLink(implementationId: Int, modelTypeId: Int, 
		created: Instant, deprecatedAfter: Option[Instant]) = 
		wrap(data.copyApiNodeMethodImplementationModelSchemaLink(implementationId, modelTypeId, created, 
			deprecatedAfter))
}

