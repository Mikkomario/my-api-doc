package vf.myapidoc.model.partial.api

import vf.myapidoc.model.factory.api.ApiNodeMethodImplementationModelSchemaLinkFactory

import java.time.Instant

/**
  * Common trait for classes which provide read and copy access to api node method implementation 
  * model schema link properties
  * @tparam Repr Implementing data class or data wrapper class
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait ApiNodeMethodImplementationModelSchemaLinkDataLike[+Repr] 
	extends HasApiNodeMethodImplementationModelSchemaLinkProps 
		with ApiNodeMethodImplementationModelSchemaLinkFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Builds a modified copy of this api node method implementation model schema link
	  * @param implementationId New implementation id to assign. Default = current value.
	  * @param modelTypeId      New model type id to assign. Default = current value.
	  * @param created          New created to assign. Default = current value.
	  * @param deprecatedAfter  New deprecated after to assign. Default = current value.
	  * @return A copy of this api node method implementation model schema link with the specified properties
	  */
	def copyApiNodeMethodImplementationModelSchemaLink(implementationId: Int = implementationId, 
		modelTypeId: Int = modelTypeId, created: Instant = created, 
		deprecatedAfter: Option[Instant] = deprecatedAfter): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withCreated(created: Instant) = copyApiNodeMethodImplementationModelSchemaLink(created = 
		created)
	
	override def withDeprecatedAfter(deprecatedAfter: Instant) = 
		copyApiNodeMethodImplementationModelSchemaLink(deprecatedAfter = Some(deprecatedAfter))
	
	override def withImplementationId(implementationId: Int) = 
		copyApiNodeMethodImplementationModelSchemaLink(implementationId = implementationId)
	
	override def withModelTypeId(modelTypeId: Int) = 
		copyApiNodeMethodImplementationModelSchemaLink(modelTypeId = modelTypeId)
}

