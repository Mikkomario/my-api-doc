package vf.myapidoc.model.factory.api

/**
  * Common trait for classes that implement ResponseBodySchemaFactory by wrapping a 
  * ResponseBodySchemaFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait ResponseBodySchemaFactoryWrapper[A <: ResponseBodySchemaFactory[A], +Repr] 
	extends ResponseBodySchemaFactory[Repr] with BodySchemaFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withStatusId(statusId: Int) = withParentId(statusId)
}

