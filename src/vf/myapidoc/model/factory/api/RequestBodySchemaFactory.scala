package vf.myapidoc.model.factory.api

/**
  * Common trait for request body schema-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.10.2026, v0.1
  */
trait RequestBodySchemaFactory[+A] extends BodySchemaFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param customContentType New custom content type to assign
	  * @return Copy of this item with the specified custom content type
	  */
	def withCustomContentType(customContentType: String): A
	/**
	  * @param description New description to assign
	  * @return Copy of this item with the specified description
	  */
	def withDescription(description: String): A
	/**
	  * @param implementationId New implementation id to assign
	  * @return Copy of this item with the specified implementation id
	  */
	def withImplementationId(implementationId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withParentId(parentId: Int) = withImplementationId(parentId)
}

