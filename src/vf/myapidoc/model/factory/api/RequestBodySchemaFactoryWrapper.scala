package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement RequestBodySchemaFactory by wrapping a 
  * RequestBodySchemaFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait RequestBodySchemaFactoryWrapper[A <: RequestBodySchemaFactory[A], +Repr] 
	extends RequestBodySchemaFactory[Repr] 
		with ApiNodeMethodImplementationModelSchemaLinkFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withCustomContentType(customContentType: String) = 
		mapWrapped { _.withCustomContentType(customContentType) }
	
	override def withDescription(description: String) = mapWrapped { _.withDescription(description) }
	
	override def withIsArray(isArray: Boolean) = mapWrapped { _.withIsArray(isArray) }
}

