package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement QueryParametersSchemaLinkFactory by wrapping a 
  * QueryParametersSchemaLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait QueryParametersSchemaLinkFactoryWrapper[A <: QueryParametersSchemaLinkFactory[A], +Repr] 
	extends QueryParametersSchemaLinkFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withImplementationId(implementationId: Int) = 
		mapWrapped { _.withImplementationId(implementationId) }
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
}

