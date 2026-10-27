package vf.myapidoc.model.factory.api

/**
  * Common trait for response body schema-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 27.10.2026, v0.1
  */
trait ResponseBodySchemaFactory[+A] extends BodySchemaFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param statusId New status id to assign
	  * @return Copy of this item with the specified status id
	  */
	def withStatusId(statusId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withParentId(parentId: Int) = withStatusId(parentId)
}

