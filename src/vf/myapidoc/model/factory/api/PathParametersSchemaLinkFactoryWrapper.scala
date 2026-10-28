package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement PathParametersSchemaLinkFactory by wrapping a 
  * PathParametersSchemaLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait PathParametersSchemaLinkFactoryWrapper[A <: PathParametersSchemaLinkFactory[A], +Repr] 
	extends PathParametersSchemaLinkFactory[Repr] with VersionedDocElementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withModelTypeId(modelTypeId: Int) = mapWrapped { _.withModelTypeId(modelTypeId) }
	override def withNodeId(nodeId: Int) = mapWrapped { _.withNodeId(nodeId) }
}

