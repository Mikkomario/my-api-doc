package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement ApiNodePathFactory by wrapping another such instance
  * @tparam Data Type of the wrapped data
  * @tparam Repr Type of the implementing class
  * @author Mikko Hilpinen
  * @since 15.11.2026, v0.1
  */
trait ApiNodePathFactoryWrapper[Data <: ApiNodePathFactory[Data], +Repr] 
	extends ApiNodePathFactory[Repr] with VersionedDocElementFactoryWrapper[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withNodeId(nodeId: Int) = mapData { _.withNodeId(nodeId) }
	
	override def withParamsModelSchemaId(paramsModelSchemaId: Int) = 
		mapData { _.withParamsModelSchemaId(paramsModelSchemaId) }
	
	override def withRelative(relative: String) = mapData { _.withRelative(relative) }
}

