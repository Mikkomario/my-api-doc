package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement PathParametersSchemaLinkFactory by wrapping another such instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
@deprecated("Replaced with ApiNodePathFactoryWrapper", "v0.1")
trait PathParametersSchemaLinkFactoryWrapper[Data <: PathParametersSchemaLinkFactory[Data], +Repr]
	extends PathParametersSchemaLinkFactory[Repr] with VersionedDocElementFactoryWrapper[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withModelTypeId(modelTypeId: Int) = mapData { _.withModelTypeId(modelTypeId) }
	override def withPathId(pathId: Int) = mapData { _.withPathId(pathId) }
}

